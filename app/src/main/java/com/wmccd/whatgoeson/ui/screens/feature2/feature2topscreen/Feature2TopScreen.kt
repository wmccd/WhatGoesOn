package com.wmccd.whatgoeson.ui.screens.feature2.feature2topscreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.ui.screens.common.DisplayError
import com.wmccd.whatgoeson.ui.screens.common.DisplayLoading
import com.wmccd.whatgoeson.ui.screens.common.NavigationEvent
import java.util.UUID

@Composable
fun Feature2TopScreen(
    navController: NavHostController,
    viewModel: Feature2TopScreenViewModel = Feature2TopScreenViewModel()
) {

    // Listen for navigation events sent by the ViewModel
    LaunchedEffect(key1 = UUID.randomUUID().toString()) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToNextScreen -> {
                    //navController.navigate(NavigationEnum.FeatureScreen1a.route)
                }
            }
        }
    }

    DisplayContent(viewModel)
}

@Composable
private fun DisplayContent(viewModel: Feature2TopScreenViewModel) {
    // Display content based on uiState
    val uiState by viewModel.uiState.collectAsState()
    MyApplication.utilities.logger.log(Log.INFO, "TopScreen1", "DisplayContent $uiState")
    when {
        uiState.isLoading -> DisplayLoading()
        uiState.error != null -> DisplayError(uiState.error)
        uiState.data != null -> DisplayData(viewModel)
    }
}

@Composable
fun DisplayData(viewModel: Feature2TopScreenViewModel) {
    //Display the data that was fetched
    val uiState by viewModel.uiState.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    viewModel.onEvent(Feature2TopScreenEvents.ButtonClicked)
                }
            ) {
                Text(text = "Click Me")
            }
        }
    }
}
