package com.wmccd.whatgoeson.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorComposable
import com.wmccd.whatgoeson.R

//Defines the routes and screens that can be navigated to
enum class NavigationEnum(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int? = null,
    val bottomTabIcon: ImageVector = Icons.Default.Warning,
    val topLevelScreen: Boolean = false,
    val closeIcon: ImageVector = Icons.Default.Close,
    val featureScreen: Boolean = true,

    ) {
    Feature1TopScreen(
        route = "Feature1TopScreen",
        title = R.string.feature_1_top_screen,
        bottomTabIcon = Icons.Default.Home,
        topLevelScreen= true,
        featureScreen = false
    ),
    Feature2TopScreen(
        route = "Feature2TopScreen",
        title = R.string.feature_2_top_screen,
        bottomTabIcon = Icons.Default.Person,
        topLevelScreen = true,
        featureScreen = false
    ),
    Feature3TopScreen(
        route = "Feature3TopScreen",
        title = R.string.feature_3_top_screen,
        bottomTabIcon = Icons.Default.Menu,
        topLevelScreen = true,
        featureScreen = false
    ),
    Feature1SubScreen1(
        route = "Feature2SubScreen1",
        title = R.string.feature_1_sub_screen_1,
    ),
    Feature1SubScreen2(
        route = "Feature2SubScreen2",
        title = R.string.feature_1_sub_screen_2,
    );

    companion object {
        fun fromRoute(route: String?): NavigationEnum? {
            return entries.find { it.route == route }
        }
    }
}