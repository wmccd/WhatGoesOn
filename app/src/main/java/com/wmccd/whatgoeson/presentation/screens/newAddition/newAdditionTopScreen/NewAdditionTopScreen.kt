package com.wmccd.whatgoeson.presentation.screens.newAddition.newAdditionTopScreen

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
import com.wmccd.whatgoeson.presentation.theme.MyAppTheme
import java.util.UUID

@Composable
fun NewAdditionTopScreen(
    navController: NavHostController,
    viewModel: NewAdditionTopScreenViewModel = NewAdditionTopScreenViewModel()
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
private fun DisplayContent(viewModel: NewAdditionTopScreenViewModel) {
    // Display content based on uiState
    val uiState by viewModel.uiState.collectAsState()
    when {
        uiState.isLoading -> DisplayLoading()
        uiState.error != null -> DisplayError(uiState.error)
        uiState.data != null -> DisplayData(
            uiState= uiState,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun DisplayData(
    uiState: NewAdditionTopScreenUiState,
    onEvent: (NewAdditionTopScreenEvents) -> Unit = {},
) {
    if(uiState.data == null) {
        DisplayError(stringResource(R.string.no_data_to_display))
    } else {
        val data = uiState.data
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
                        onEvent(NewAdditionTopScreenEvents.ButtonClicked)
                    }
                ) {
                    Text(text = "Click Me")
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDisplayData(){
    MyAppTheme {
        DisplayData(
            uiState = NewAdditionTopScreenUiState(
               data = NewAdditionTopScreenUiData(
                   someData = "Hello"
               ),
            )
        )
    }
}
