package com.wmccd.whatgoeson.presentation.screens.albumList

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
import com.wmccd.whatgoeson.presentation.screens.NavigationEnum
import com.wmccd.whatgoeson.presentation.screens.common.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import java.util.UUID

@Composable
fun AlbumListScreen(
    navController: NavHostController,
    viewModel: AlbumListViewModel = AlbumListViewModel()
) {

    // Listen for navigation events sent by the ViewModel
    LaunchedEffect(key1 = UUID.randomUUID().toString()) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToNextScreen -> {
                    //TODO Add Destination Route
                    //navController.navigate(NavigationEnum.AddDestination.route)
                }
            }
        }
    }
    DisplayContentMode(viewModel)
}

@Composable
private fun DisplayContentMode(viewModel: AlbumListViewModel) {
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
    uiState: AlbumListUiState,
    onEvent: (AlbumListEvents) -> Unit = {},
) {
    if (uiState.data == null) {
        DisplayError(stringResource(R.string.no_data_to_display))
    } else {
        DisplayData(uiState.data, onEvent)
    }
}

@Composable
fun DisplayData(
    data: AlbumListUiData,
    onEvent: (AlbumListEvents) -> Unit = {},
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
                    onEvent(AlbumListEvents.ButtonClicked)
                }
            ) {
                Text(text = "Click Me")
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDisplayData() {
    PreviewTheme {
        DisplayContent(
            uiState = AlbumListUiState(
                data = AlbumListUiData(
                    someData = "Hello"
                ),
            )
        )
    }
}