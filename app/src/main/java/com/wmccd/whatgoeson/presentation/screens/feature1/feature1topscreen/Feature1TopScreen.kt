package com.wmccd.whatgoeson.presentation.screens.feature1.feature1topscreen

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
import com.wmccd.whatgoeson.presentation.screens.NavigationEnum
import com.wmccd.whatgoeson.presentation.screens.common.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.InternetImage
import com.wmccd.whatgoeson.presentation.screens.common.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import java.util.UUID

@Composable
fun Feature1TopScreen(
    navController: NavHostController,
    viewModel: Feature1TopScreenViewModel = Feature1TopScreenViewModel()
) {

    // Listen for navigation events sent by the ViewModel
    LaunchedEffect(key1 = UUID.randomUUID().toString()) {
        viewModel.messageFlow.collect { message ->
            when (message) {
                is NavigationEvent.NavigateToNextScreen -> {
                     navController.navigate(NavigationEnum.Feature1SubScreen1.route)
                }
            }
        }
    }
    DisplayContent(viewModel)
}

@Composable
private fun DisplayContent(viewModel: Feature1TopScreenViewModel) {
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
fun DisplayData(viewModel: Feature1TopScreenViewModel) {
    //Display the data that was fetched
    val uiState by viewModel.uiState.collectAsState()
    val data = uiState.data
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InternetImage(
                imageUrl = "https://upload.wikimedia.org/wikipedia/en/0/01/Bob_Dylan_-_Oh_Mercy.jpg?20180114224124"
            )
            Text(text = data?.randomText.orEmpty())
            Text(text = data?.randomLong.toString())
            Text(text = data?.randomInt.toString())
            Button(
                onClick = {
                    viewModel.onEvent(Feature1TopScreenEvents.ButtonClicked)
                }
            ) {
                Text(text = "Click Me")
            }
        }
    }
}
