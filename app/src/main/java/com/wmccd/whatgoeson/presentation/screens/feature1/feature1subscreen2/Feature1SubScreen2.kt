package com.wmccd.whatgoeson.presentation.screens.feature1.feature1subscreen2

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import java.util.UUID

@Composable
fun Feature1SubScreen2(
    navController: NavHostController,
    viewModel: Feature1SubScreen2ViewModel = Feature1SubScreen2ViewModel()
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
    DetermineDisplayMode(viewModel)
}

@Composable
private fun DetermineDisplayMode(viewModel: Feature1SubScreen2ViewModel) {
    // Display content based on uiState
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> DisplayLoading()
        uiState.error != null -> DisplayError(uiState.error)
        uiState.data != null -> DisplayContent(
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun DisplayContent(
    uiState: Feature1SubScreen2UiState,
    onEvent: (Feature1SubScreen2Events) -> Unit = {}
) {
    if(uiState.data == null) {
        DisplayError(stringResource(R.string.no_data_to_display))
    } else {
        DisplayData(uiState.data, onEvent)
    }
}

@Composable
private fun DisplayData(
    data: Feature1SubScreen2UiData,
    onEvent: (Feature1SubScreen2Events) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = data.someData.orEmpty())
            Button(
                onClick = {
                    onEvent(Feature1SubScreen2Events.ButtonClicked)
                }
            ) {
                Text(text = "Click Me" + data.someData.orEmpty())
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDisplayData(){
    PreviewTheme {
        DisplayContent(
            uiState = Feature1SubScreen2UiState(
                data = Feature1SubScreen2UiData(
                    someData = "Hello"
                ),
            )
        )
    }
}
