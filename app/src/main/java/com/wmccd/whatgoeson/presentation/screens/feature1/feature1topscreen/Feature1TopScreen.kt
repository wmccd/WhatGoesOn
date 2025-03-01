package com.wmccd.whatgoeson.presentation.screens.feature1.feature1topscreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.NavigationEnum
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.composables.MyInternetImage
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.presentation.screens.common.STANDARD_SCREEN_PADDING
import java.util.UUID

@Composable
fun Feature1TopScreen(
    navController: NavHostController,
    viewModel: Feature1TopScreenViewModel = Feature1TopScreenViewModel()
) {

    // Listen for navigation events sent by the ViewModel
    LaunchedEffect(key1 = UUID.randomUUID().toString()) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToNextScreen -> {
                     navController.navigate(NavigationEnum.Feature1SubScreen1.route)
                }
            }
        }
    }
    DetermineDisplayMode(viewModel)
}

@Composable
private fun DetermineDisplayMode(viewModel: Feature1TopScreenViewModel) {
    // Display content based on uiState
    val uiState by viewModel.uiState.collectAsState()
    MyApplication.utilities.logger.log(Log.INFO, "TopScreen1", "DisplayContent $uiState")
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
    uiState: Feature1TopScreenUiState,
    onEvent: (Feature1TopScreenEvents) -> Unit = {},
) {
    if(uiState.data == null) {
        DisplayError(stringResource(R.string.no_data_to_display))
    } else {
        DisplayData(uiState.data, onEvent)
    }
}

@Composable
private fun DisplayData(
    data: Feature1TopScreenUiData,
    onEvent: (Feature1TopScreenEvents) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(STANDARD_SCREEN_PADDING),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyInternetImage(
                imageUrl = "https://upload.wikimedia.org/wikipedia/en/0/01/Bob_Dylan_-_Oh_Mercy.jpg?20180114224124"
            )
            Text(text = data.randomText.orEmpty())
            Text(text = data.randomLong.toString())
            Text(text = data.randomInt.toString())
            Button(
                onClick = {
                    onEvent(Feature1TopScreenEvents.ButtonClicked)
                }
            ) {
                Text(text = "Click Me")
            }
        }
    }
}

@Composable
@Preview
private fun PreviewDisplayData(){
    PreviewTheme {
        DisplayContent(
            uiState = Feature1TopScreenUiState(
                data = Feature1TopScreenUiData(
                    randomText = "Hello",
                    randomLong = System.currentTimeMillis(),
                    randomInt = 123
                ),
            )
        )
    }
}