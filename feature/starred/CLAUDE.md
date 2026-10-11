# Starred Feature

Locally cached view of user's starred repos. **Presentation-only** — uses `StarredRepository` from `core/domain` directly.

## Structure

```
feature/starred/presentation/
├── StarredReposViewModel / State / Action / Root
├── model/StarredRepositoryUi
├── mappers/StarredRepoToUiMapper
├── utils/TimeFormatUtils
└── components/StarredRepositoryItem
```

## Deps

- `StarredRepository`, `FavouritesRepository`, `AuthenticationState` (core/domain)
- `ProfileRepository` (feature/profile/domain) — current user login for E20 self-owned badge
- Local Room (`StarredRepoDao` in core/data)

## Navigation

`GithubStoreGraph.StarredReposScreen`.

## Notes

- Periodic sync against GitHub's `/user/starred` (gated on `isAuthenticated`). Local Room mirror is the read source.
- UI model `StarredRepositoryUi` mapped from domain `StarredRepository`.
- Starring happens elsewhere (home/details/search). This module displays + removes.
- Inline search bar (E562) when list non-empty — filters by name/owner/description/language client-side. `OnRefresh` clears active query so refreshed list isn't masked behind stale filter.
- `StarredRepositoryUi.isCurrentUserOwner` set when signed-in user owns the repo (E20).
- Koin module registered in `composeApp/.../app/di/ViewModelsModule.kt` (no `data/di/`).
