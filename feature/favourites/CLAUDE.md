# Favourites Feature

Local saved favorites view. **Presentation-only** — uses `FavouritesRepository` from `core/domain` directly.

## Structure

```
feature/favourites/presentation/
├── FavouritesViewModel / State / Action / Root
├── model/FavouriteRepository
├── mappers/FavouriteRepositoryMapper
└── components/FavouriteRepositoryItem
```

## Deps

- `FavouritesRepository` (core/domain) — CRUD
- `ProfileRepository` (feature/profile/domain) — E20 self-owned badge
- Local Room (`FavoriteRepoDao` in core/data)

## Navigation

`GithubStoreGraph.FavouritesScreen`.

## Notes

- No network. All local Room.
- Adding to favourites happens elsewhere (home/details/search). This module displays + removes.
- Inline search bar (E562) when list non-empty — filters by name/owner/description/language client-side.
- `FavouriteRepository.isCurrentUserOwner` set when signed-in user owns the repo (E20).
- Koin module registered in `composeApp/.../app/di/ViewModelsModule.kt` (no `data/di/`).
