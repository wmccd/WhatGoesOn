package com.wmccd.whatgoeson.presentation.screens

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import com.wmccd.whatgoeson.R

//Defines the routes and screens that can be navigated to
enum class NavigationEnum(
    val route: String,
    @StringRes val topBarTitle: Int,
    val topBarNavigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    val topBarCloseIcon: ImageVector = Icons.Default.Close,
    val bottomTabIcon: ImageVector = Icons.Default.Warning,
    val topLevelScreen: Boolean = false,
) {
    HomeScreen(
        route = "HomeScreen",
        topBarTitle = R.string.random_record,
        bottomTabIcon = Icons.Default.Home,
        topLevelScreen= true,
    ),
    Feature1TopScreen(
        route = "Feature1TopScreen",
        topBarTitle = R.string.feature_1_top_screen,
        bottomTabIcon = Icons.Filled.PlayArrow,
        topLevelScreen= true,
    ),
    NewAlbumScreen(
        route = "NewAlbumScreen",
        topBarTitle = R.string.new_album,
        bottomTabIcon = Icons.Default.Add,
        topLevelScreen = true,
    ),
    StatsScreen(
        route = "StatsScreen",
        topBarTitle = R.string.stats,
        bottomTabIcon = Icons.Default.Info,
        topLevelScreen = true,
    ),
    AlbumListScreen(
        route = "AlbumListScreen",
        topBarTitle = R.string.album_list,
        bottomTabIcon = Icons.AutoMirrored.Filled.List,
        topLevelScreen = true,
    ),
    Feature3TopScreen(
        route = "Feature3TopScreen",
        topBarTitle = R.string.feature_3_top_screen,
        bottomTabIcon = Icons.Default.Menu,
        topLevelScreen = true,
    ),
    Feature1SubScreen1(
        route = "Feature2SubScreen1",
        topBarTitle = R.string.feature_1_sub_screen_1,
    ),
    Feature1SubScreen2(
        route = "Feature2SubScreen2",
        topBarTitle = R.string.feature_1_sub_screen_2,
    );

    companion object {
        fun fromRoute(route: String?): NavigationEnum? {
            return entries.find { it.route == route }
        }
    }
}