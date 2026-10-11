# Auth Feature

Three GitHub sign-in paths, picked per session: **web OAuth + PKCE + handoff** (primary), **device flow** (fallback), **Personal Access Token paste** (last resort). Works on Android + Desktop.

## Structure

```
feature/auth/
├── domain/repository/AuthenticationRepository.kt        # contract + AuthPath, DeviceFlowStart, PollOutcome, WebAuthRegistration, PatRejectedException
├── data/
│   ├── crypto/PkceGenerator.kt                          # SHA-256 PKCE triplet (state + verifier + challenge)
│   ├── network/GitHubAuthApi.kt                         # direct + backend-proxied device-flow endpoints
│   ├── network/WebAuthApi.kt                            # web-OAuth register + handoff consume
│   ├── repository/AuthenticationRepositoryImpl.kt
│   └── di
└── presentation/
    ├── AuthenticationViewModel / State / Action / Event / Root
    ├── AuthDeepLinkBus.kt                               # SharedFlow bridge from app-level deep-link parser to VM
    └── components/
```

## Key interface

```kotlin
interface AuthenticationRepository {
    val accessTokenFlow: Flow<String?>

    suspend fun startDeviceFlow(): DeviceFlowStart
    suspend fun awaitDeviceToken(start: GithubDeviceStart): GithubDeviceTokenSuccess
    suspend fun pollDeviceTokenOnce(deviceCode: String, path: AuthPath): PollOutcome

    suspend fun signInWithPat(token: String): Result<Unit>

    suspend fun registerWebAuth(): Result<WebAuthRegistration>
    suspend fun exchangeWebAuthHandoff(handoffId: String): Result<String>
}

enum class AuthPath { Backend, Direct }
```

## Navigation

`GithubStoreGraph.AuthenticationScreen`.

## Web OAuth path (primary)

1. `PkceGenerator.generate()` → `(state, codeVerifier, codeChallenge)` (S256).
2. `WebAuthApi.register(state, codeChallenge, codeVerifier)` → POST `https://github-store.org/auth/register`. Cloudflare Worker stores `(state → {codeVerifier, codeChallenge})` in Workers KV with short TTL and returns `auth_url` (the `github.com/login/oauth/authorize?…` URL).
3. VM opens `auth_url` in user's browser via `BrowserHelper`.
4. User authorizes on GitHub. GitHub redirects to `https://github-store.org/auth/callback?code=…&state=…`.
5. Worker re-validates `state`, exchanges `code` against `api.github-store.org` (backend posts to GitHub `/oauth/access_token` with the stored verifier), backend writes `(handoffId → access_token)` to Postgres with 60s TTL.
6. Worker redirects browser to `githubstore://auth?h=<handoffId>` (custom scheme picked up by app via OS deep link).
7. App-level `DeepLinkParser` routes the URI through `AuthDeepLinkBus`. VM calls `exchangeWebAuthHandoff(handoffId)` → `WebAuthApi.consumeHandoff` → POST `api.github-store.org/v1/oauth/handoff/<id>` (atomic `DELETE…RETURNING` — single-use). Token lands in `TokenStore`.

Custom scheme is public — any app can fire `githubstore://auth?…`. The `state` parameter and 60s server-side TTL are the integrity guards; without a live KV entry the handoff is a no-op.

## Device flow path (fallback)

Used when web flow can't complete (browser unreachable, deep link not registered, user explicitly picks "Use device code instead").

- Primary sub-path: backend proxy `/v1/auth/device/start` + `/poll` on `api.github-store.org` for networks throttling `github.com` (China, corporate filters).
- Each session picks one `AuthPath` (`Backend` | `Direct`), persists in `SavedStateHandle`. Only escalates `Backend → Direct` on infra errors (timeout / 5xx / 429-without-Retry-After). HTTP 4xx and GitHub negative 200-bodies (`authorization_pending`, `slow_down`, `access_denied`, `expired_token`, `bad_verification_code`) are real answers — never trigger fallback.
- Backend rate limits hard: 10 starts/hr, 200 polls/hr per IP. Don't add retry loops on top of Ktor's `HttpRequestRetry(maxRetries = 2)`.
- Backend responses carry `X-Request-ID` — `GitHubAuthApi` embeds it in error messages via `asRequestIdTag()` for cross-log correlation.

## PAT path (last resort)

`signInWithPat(token)`:
1. Client-side format check (rejects obvious paste-errors — needs `ghp_` / `github_pat_` prefix).
2. Network check against GitHub `/user`. 401 → `PatRejectedException(BadCredentials)`, 403 → `InsufficientScope`, other non-2xx → `Other(statusCode)`. On reject the sheet stays open.
3. If GitHub is unreachable (timeout/DNS/block) the token persists optimistically — the whole reason this path exists is for users who can't reach GitHub reliably. A bad-but-unreachable token surfaces a 401 on the first authenticated call.

## Notes

- Token stored via `TokenStore` (DataStore-backed). `accessTokenFlow` observed app-wide.
- `GITHUB_CLIENT_ID` in `local.properties` for builds — must match the OAuth App registered on the backend and used by the Worker.
- Endpoints: `WEB_ORIGIN = https://github-store.org`, `BACKEND_ORIGIN = https://api.github-store.org`. Constants in `core/data/network/BackendEndpoints.kt`.
- Android deep-link filters in `composeApp/src/androidMain/AndroidManifest.xml`: scheme `githubstore`, hosts `auth`, `callback`, `repo`, `apps`. Desktop registers the protocol via `DesktopDeepLink` on Win/Linux (macOS uses the bundle Info.plist `CFBundleURLTypes`).
