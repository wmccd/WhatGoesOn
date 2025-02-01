package com.wmccd.whatgoeson.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.NavigationEnum
import com.wmccd.whatgoeson.presentation.screens.common.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.STANDARD_SCREEN_PADDING
import com.wmccd.whatgoeson.presentation.screens.newAddition.newAdditionTopScreen.NewAdditionTopScreenUiData
import com.wmccd.whatgoeson.presentation.screens.newAddition.newAdditionTopScreen.NewAdditionTopScreenUiState
import com.wmccd.whatgoeson.presentation.theme.MyAppTheme
import java.util.UUID

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = HomeViewModel()
) {

    // Listen for navigation events sent by the ViewModel
    LaunchedEffect(key1 = UUID.randomUUID().toString()) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToNextScreen -> {
                    navController.navigate(NavigationEnum.Feature1TopScreen.route)
                }
            }
        }
    }
    DisplayContent(viewModel)
}

@Composable
private fun DisplayContent(viewModel: HomeViewModel) {
    // Display content based on uiState
    val uiState by viewModel.uiState.collectAsState()
    when {
        uiState.isLoading -> DisplayLoading()
        uiState.error != null -> DisplayError(uiState.error)
        uiState.data != null -> DisplayData(
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
private fun DisplayData(
    uiState: HomeUiState,
    onEvent: (HomeEvents) -> Unit = {}
) {
    //Display the data that was fetched
    if(uiState.data == null) {
        DisplayError(stringResource(R.string.no_data_to_display))
    }
    val data = uiState.data
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(STANDARD_SCREEN_PADDING),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(data?.noAlbumsStored == true){
                Text(
                    text = stringResource(id = R.string.it_looks_like_you_are_new_here),
                    textAlign = TextAlign.Center
                )
            }else{
                Text(text = "Artist: ${data?.artistName}")
                Text(text = "Album: ${data?.albumName}")
            }

        }
    }
}

@Preview
@Composable
private fun PreviewDisplayData(){
    MyAppTheme {
        DisplayData(
            uiState = HomeUiState(
                data = HomeUiData(
                    artistName = "Artist",
                    albumName = "Album",
                    albumArtUrl = null,
                    noAlbumsStored = false
                ),
            )
        )
    }
}
