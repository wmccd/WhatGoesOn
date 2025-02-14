package com.wmccd.whatgoeson.presentation.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.albumList.AlbumListScreen
import com.wmccd.whatgoeson.presentation.screens.feature1.feature1subscreen1.Feature1SubScreen1
import com.wmccd.whatgoeson.presentation.screens.feature1.feature1subscreen2.Feature1SubScreen2
import com.wmccd.whatgoeson.presentation.screens.feature1.feature1topscreen.Feature1TopScreen
import com.wmccd.whatgoeson.presentation.screens.newAlbum.NewAlbumScreen
import com.wmccd.whatgoeson.presentation.screens.feature3.feature3topscreen.Feature3TopScreen
import com.wmccd.whatgoeson.presentation.screens.home.HomeScreen
import com.wmccd.whatgoeson.presentation.theme.MyAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    /*A single activity app is recommended now. For this to work I need to set up two things here:
    1. A NavHost - this defines all routes and screens in the app
    2. A NavHostController - this is used to navigate between screens
    3. selectedScreen - this is used to keep track of the currently selected screen
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //If you set up a theme by hand there are dozens of entries to set up.
            //Use the Material 3 Theme Builder to help get you started:
            //https://m3.material.io/theme-builder#/custom
            MyAppTheme {

                //The main object for navigating between screens
                val navController = rememberNavController()

                //Keeps track of the currently selected screen
                val selectedScreen = remember { mutableStateOf(NavigationEnum.HomeScreen) }

                //Listens for changes to the current screen and updates the selectedScreen variable
                ScreenChangeListener(navController = navController, selectedScreen = selectedScreen)

                //Determines what to display on screen
                DisplayScreenContent(navController = navController, selectedScreen = selectedScreen)
            }
        }
    }
}

@Composable
private fun DisplayScreenContent(
    navController: NavHostController,
    selectedScreen: MutableState<NavigationEnum>
) {
    //The body of the UI is surrounded by a Scaffold to allow for the Top Bar, Bottom Bar, and FAB
    Scaffold(
        topBar = {
            DisplayTopBar(navController = navController, selectedScreen = selectedScreen.value)
        },
        bottomBar = {
            DisplayBottomBar(navController = navController, selectedScreen = selectedScreen.value)
        },
        floatingActionButton = {
            DisplayFloatingActionButton(navController = navController, selectedScreen = selectedScreen.value)
        }
    ) { innerPadding ->
        NavigationControl(navController = navController, innerPadding = innerPadding)
    }
}

@Composable
private fun ScreenChangeListener(
    navController: NavHostController,
    selectedScreen: MutableState<NavigationEnum>
) {
    //Listens for changes to the current screen and updates the selectedScreen variable
    navController.addOnDestinationChangedListener { _, destination, _ ->
        val screenBeingDisplayed = NavigationEnum.fromRoute(destination.route)?: NavigationEnum.HomeScreen
        selectedScreen.value = screenBeingDisplayed
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DisplayTopBar(
    navController: NavHostController,
    selectedScreen: NavigationEnum
) {
    //Displays the Top Bar
    TopAppBar(
        title = {
            DisplayTitle(selectedScreen = selectedScreen)
        },
        navigationIcon = {
            DisplayNavigationIcon(navController = navController, selectedScreen = selectedScreen)
        },
        actions = {
            DisplayActionIcon(navController = navController, selectedScreen = selectedScreen)
        }
    )
}

@Composable
private fun DisplayTitle(selectedScreen: NavigationEnum) {
    //Displays the title of the current screen
    Text(text = stringResource(id = selectedScreen.topBarTitle))
}

@Composable
private fun DisplayNavigationIcon(
    navController: NavHostController,
    selectedScreen: NavigationEnum
) {
    //Displays the back button if the current screen is not the top level screen
    if (!selectedScreen.topLevelScreen) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                selectedScreen.topBarNavigationIcon,
                contentDescription = stringResource(R.string.back)
            )
        }
    }
}

@Composable
private fun CheckBeforeClosing(
    navController: NavHostController,
    showDialog: MutableState<Boolean>
) {
    //Displays a confirmation dialog before closing the current screen and going back to main screen
    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
        },
        title = {
            Text(text = stringResource(R.string.are_you_sure))
        },
        text = {
            Text(text = stringResource(R.string.this_action_cant_be_undone))
       },
        confirmButton = {
            //Displays the "Delete" button to confirm the action
            Button(onClick = {
                showDialog.value = false

                //Deletes the current screen and goes back to the main screen
                navController.navigate(NavigationEnum.HomeScreen.route) {
                    popUpTo(NavigationEnum.HomeScreen.route) { inclusive = true }
                    launchSingleTop = true
                }

            }) {
                Text(text = stringResource(R.string.carry_on))
            }
        },
        dismissButton = {
            //Displays the "Cancel" button to cancel the action
            Button(onClick = { showDialog.value = false }) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun DisplayActionIcon(
    navController: NavHostController,
    selectedScreen: NavigationEnum
) {
    //Displays the close button if the current screen is not a top level screen
    if (!selectedScreen.topLevelScreen) {

        //Determines if the "Are you sure dialog should display
        var showDialog = remember { mutableStateOf(false) }

        IconButton(onClick = {
            showDialog.value = true
        }) {
            Icon(
                imageVector = selectedScreen.topBarCloseIcon,
                contentDescription = stringResource(R.string.close)
            )
        }

        if (showDialog.value) {
            CheckBeforeClosing(navController, showDialog)
        }
    }
}

@Composable
private fun DisplayBottomBar(
    navController: NavHostController,
    selectedScreen: NavigationEnum
) {
    //Displays the bottom bar for the app and includes a button for each top level screen
    if (selectedScreen.topLevelScreen) {
        BottomAppBar {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DisplayBottomBarItem(navController, NavigationEnum.HomeScreen, selectedScreen)
                DisplayBottomBarItem(navController, NavigationEnum.Feature1TopScreen, selectedScreen)
                DisplayBottomBarItem(navController, NavigationEnum.NewAlbumScreen, selectedScreen)
                DisplayBottomBarItem(navController, NavigationEnum.Feature3TopScreen, selectedScreen)
                DisplayBottomBarItem(navController, NavigationEnum.AlbumListScreen, selectedScreen)
            }
        }
    }
}

private @Composable
fun DisplayFloatingActionButton(
    navController: NavHostController,
    selectedScreen: NavigationEnum
) {
    //Displays the floating action button on a top level screen when needed
    var albumCount = 0
    runBlocking {
        albumCount = MyApplication.repository.appDatabase.albumDao().getAlbumCount().first()
    }
    if (albumCount == 0) {
        if (selectedScreen.topLevelScreen) {
            FloatingActionButton(onClick = {
                when (selectedScreen) {
                    NavigationEnum.HomeScreen -> navController.navigate(NavigationEnum.NewAlbumScreen.route)
                    NavigationEnum.Feature1TopScreen -> navController.navigate(NavigationEnum.Feature1SubScreen1.route)
                    else -> {}
                }
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add))
            }
        }
    }
}

@Composable
private fun NavigationControl(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    //Acts like a content page for all the screens that can be navigated to
    NavHost(
        navController = navController,
        startDestination = NavigationEnum.HomeScreen.route, //This is the first screen that will be displayed
        modifier = Modifier.padding(innerPadding)
    ) {
        //Declares all the screens that can be navigated to
        composable(NavigationEnum.HomeScreen.route) { HomeScreen(navController = navController) }
        composable(NavigationEnum.Feature1TopScreen.route) { Feature1TopScreen(navController = navController) }
        composable(NavigationEnum.NewAlbumScreen.route) { NewAlbumScreen(navController = navController) }
        composable(NavigationEnum.Feature3TopScreen.route) { Feature3TopScreen(navController = navController) }
        composable(NavigationEnum.Feature1SubScreen1.route) { Feature1SubScreen1(navController = navController) }
        composable(NavigationEnum.Feature1SubScreen2.route) { Feature1SubScreen2(navController = navController) }
        composable(NavigationEnum.AlbumListScreen.route) { AlbumListScreen(navController = navController) }
    }
}

@Composable
private fun DisplayBottomBarItem(
    navController: NavHostController,
    navigationEnum: NavigationEnum,
    selectedScreen: NavigationEnum
) {
    //Displays a button for each top level screen and changes the color of the button if it belongs to the current screen
    IconButton(
        onClick = {
            navController.navigate(navigationEnum.route)
        }
    ) {
        Icon(
            imageVector = navigationEnum.bottomTabIcon,
            contentDescription = stringResource( navigationEnum.topBarTitle),
            tint = if (selectedScreen == navigationEnum) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyAppTheme {
        DisplayBottomBarItem(
            NavHostController(MyApplication.appContext),
            NavigationEnum.Feature1TopScreen,
            NavigationEnum.Feature1TopScreen
        )
    }
}