package com.wmccd.whatgoeson.presentation.screens.feature1.feature1subscreen1

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
import com.wmccd.whatgoeson.presentation.screens.NavigationEnum
import com.wmccd.whatgoeson.presentation.screens.common.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import java.util.UUID

@Composable
fun Feature1SubScreen1(
    navController: NavHostController,
    viewModel: Feature1SubScreen1ViewModel = Feature1SubScreen1ViewModel()
) {
    // Listen for navigation events sent by the ViewModel
    LaunchedEffect(key1 = UUID.randomUUID().toString()) {
        viewModel.messageFlow.collect { message ->
            when (message) {
                is NavigationEvent.NavigateToNextScreen -> {
                    navController.navigate(NavigationEnum.Feature1SubScreen2.route)
                }
            }
        }
    }
    DisplayContent(viewModel)
}

@Composable
private fun DisplayContent(viewModel: Feature1SubScreen1ViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    when {
        uiState.isLoading -> DisplayLoading()
        uiState.error != null -> DisplayError(uiState.error)
        uiState.data != null -> DisplayData(viewModel)
    }
}



@Composable
fun DisplayData(viewModel: Feature1SubScreen1ViewModel) {

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
                    viewModel.onEvent(Feature1SubScreen1Events.ButtonClicked)
                }
            ) {
                Text(text = "Click Me")
            }
            //Box(modifier = Modifier.fillMaxWidth().height(50.dp).background(MaterialTheme.colorScheme.primary))
        }
    }
}