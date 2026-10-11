# Dev Profile Feature

GitHub user/dev profile view — avatar, bio, stats, their repos with filter/sort, followers/following. Reached from any repository card.

## Structure

```
feature/dev-profile/
├── domain/   # DeveloperProfileRepository; DeveloperProfile, DeveloperRepository, RepoFilterType (All/Sources/Forks/…), RepoSortType (Stars/Name/Updated/…)
├── data/     # impl + dto + mappers + di
└── presentation/
    ├── DeveloperProfileViewModel / State / Action / Event / Root
    └── components/  profile header, repo list, filter controls
```

## Key interface

```kotlin
interface DeveloperProfileRepository {
    suspend fun getDeveloperProfile(username: String): Result<DeveloperProfile>
    suspend fun getDeveloperRepositories(username: String): Result<List<DeveloperRepository>>
}
```

## Navigation

`GithubStoreGraph.DeveloperProfileScreen(username: String)`.

## Notes

- Profile + repos fetched in parallel.
- Client-side filter/sort.
- Both API calls return `Result<T>`.
- Reached from cards throughout app (home/search/details/favourites/starred).
