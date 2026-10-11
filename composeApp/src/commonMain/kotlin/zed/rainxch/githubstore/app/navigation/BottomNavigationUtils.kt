package zed.rainxch.githubstore.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import zed.rainxch.githubstore.core.presentation.res.*

/**
 * Simple five-tab navigation for normal users:
 * Home, Explore, Downloads, Favorites, Settings.
 *
 * Each tab maps onto an existing, fully working screen:
 *  - Home      -> ExploreScreen   (personalised release feed + search entry)
 *  - Explore   -> ChartsScreen    (trending / releases / popular charts)
 *  - Downloads -> AppsScreen      (downloads, ready-to-install and installed apps)
 *  - Favorites -> FavouritesScreen
 *  - Settings  -> ProfileScreen   (GitHub account, appearance & downloads settings, About)
 * Search is reached from the search field on Home.
 */
object BottomNavigationUtils {
    fun items(): List<BottomNavigationItem> =
        listOf(
            BottomNavigationItem(
                titleRes = Res.string.bottom_nav_home_title,
                iconOutlined = Icons.Outlined.Home,
                iconFilled = Icons.Filled.Home,
                screen = GithubStoreGraph.ExploreScreen,
            ),
            BottomNavigationItem(
                titleRes = Res.string.bottom_nav_explore_title,
                iconOutlined = Icons.Outlined.Explore,
                iconFilled = Icons.Filled.Explore,
                screen = GithubStoreGraph.ChartsScreen,
            ),
            BottomNavigationItem(
                titleRes = Res.string.downloads,
                iconOutlined = Icons.Outlined.Download,
                iconFilled = Icons.Filled.Download,
                screen = GithubStoreGraph.AppsScreen,
            ),
            BottomNavigationItem(
                titleRes = Res.string.favourites,
                iconOutlined = Icons.Outlined.FavoriteBorder,
                iconFilled = Icons.Filled.Favorite,
                screen = GithubStoreGraph.FavouritesScreen,
            ),
            BottomNavigationItem(
                titleRes = Res.string.bottom_nav_settings_title,
                iconOutlined = Icons.Outlined.Settings,
                iconFilled = Icons.Filled.Settings,
                screen = GithubStoreGraph.ProfileGraph.ProfileScreen,
            ),
        )

    fun allowedScreens(): List<BottomNavigationItem> = items()
}
