<div align="center">

<img src="media-resources/app_icon.png" width="160" alt="GitHub App Market icon" />

# GitHub App Market

**Discover Android apps and projects from GitHub.**

</div>

GitHub App Market is a simple Android app marketplace for apps that publish their releases on GitHub:
search, browse what is popular or recently updated, save favorites, download and install.

It is a customized build of the open-source **Komi Store** project (Apache License 2.0) — see [NOTICE.md](NOTICE.md).
The official GitHub App Market logo is stored in `media-resources/github_app_market_logo_official.jpg`.

## App at a glance

| | |
|---|---|
| Application name | GitHub App Market |
| Application ID | `com.githubappmarket.app` (the Kotlin namespace stays `zed.rainxch.githubstore`) |
| Version | `1.9.2-custom` (`versionCode` 190200) |
| Min / target / compile SDK | 26 / 36 / 36 |
| Gradle / AGP / Kotlin | 8.14.3 / 8.13.2 / 2.3.10 (Compose Multiplatform 1.10.3) |
| Android module | `:composeApp` |

Navigation: **Home** (feed + search field) · **Explore** (trending / releases / popular) ·
**Downloads** (downloads, ready-to-install apps and updates) · **Favorites** · **Settings**
(GitHub account, appearance, downloads, About; power-user options are under *Advanced*).

## Clay look (soft 3D)

The default look is **Claymorphism**: soft, inflated cards with a cast shadow and a glow on the lit edge, a floating
tab bar, pressed-in search fields, glossy pill buttons. It follows the system **Morning** (light) / **Night** (dark) mode, or
you can pick one in *Settings → App settings → Appearance*; the accent can be Blue (default), Red, Green, Gold or Graphite.
The previous "Manga" look is still available in the same place.
Implementation: `core/presentation/.../personality/clay/ClayStyle.kt` (plain Compose drawing: no blur, no off-screen layers,
so it stays smooth in long lists) used by the Classic-personality components (`KomiSurface`, buttons, chips, bottom bar, text field).

## Build the debug APK (AndroidIDE on a phone, or any machine)

Requirements: JDK 17 or newer, Android SDK platform **36** and build-tools **36.0.0**, internet access for the first sync.

```
./gradlew --version
./gradlew :composeApp:assembleDebug --stacktrace
```

Output: `composeApp/build/outputs/apk/debug/composeApp-debug.apk` — signed with the standard Android **debug** key.
No release keystore, password or secret is needed or stored in this repository.

Phone tips
* Memory profile is in `gradle.properties` (`org.gradle.jvmargs` 3 GB, `kotlin.daemon.jvmargs` 2 GB, no parallel module builds).
  If the build stops with *OutOfMemoryError* / *Gradle daemon disappeared*, lower them (e.g. 2560M / 1536M) and close other apps;
  on 12 GB+ phones you can raise them.
* `ktlintFormat` no longer runs before every compile. To format sources on demand: `./gradlew ktlintFormat`
  (or `-PktlintFormatOnBuild=true`).
* If AGP's `aapt2` cannot run on your device, AndroidIDE substitutes its own ARM64 `aapt2`
  (`android.aapt2FromMavenOverride`). An `aapt2` that is older than AGP 8.13.2 can fail with AAPT2 errors; use a current AndroidIDE.
* If the configuration cache causes trouble in your IDE, add `--no-configuration-cache`.

## Configuration

* `local.properties` (optional, never commit it): `GITHUB_CLIENT_ID=<your GitHub OAuth app client id>`.
  Without it the project's default public OAuth client id (device-flow sign-in) is used. Sign-in is optional.
* Home, Explore, search and sign-in helpers use the GitHub API and the original project's public backend (`https://api.github-store.org`).
  Nothing is hard-coded or mocked; if a service is unreachable the app shows a friendly error with optional technical details.

## Build status

The sources were prepared without network access and without an Android SDK, so Gradle could not be run there:
**BUILD NOT VERIFIED IN THIS ENVIRONMENT.** The first `assembleDebug` on your device is the first real compile.
The bundled `noto_sans_sc.ttf` was structurally validated (valid TrueType, 17,773,248 bytes) and is packaged unchanged.

## License

Apache License 2.0 (see `LICENSE`). Original copyright and license notices are kept; see `NOTICE.md`.
