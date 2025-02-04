package com.wmccd.whatgoeson.presentation.screens.newAddition.newAlbumTopScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.presentation.screens.common.STANDARD_SCREEN_PADDING
import com.wmccd.whatgoeson.presentation.screens.common.composables.MyDropdownMenu
import com.wmccd.whatgoeson.presentation.screens.common.composables.MyInternetImage
import com.wmccd.whatgoeson.presentation.screens.common.composables.MyOutlinedInputText
import java.util.UUID

@Composable
fun NewAlbumTopScreen(
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
        uiState.data != null -> DetermineDisplayMode(
            uiState= uiState,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun DetermineDisplayMode(
    uiState: NewAlbumTopScreenUiState,
    onEvent: (NewAlbumTopScreenEvents) -> Unit = {},
) {
    if(uiState.data == null) {
        DisplayError(stringResource(R.string.no_data_to_display))
    } else {
        DisplayData(uiState.data, onEvent)
    }
}

@Composable
private fun DisplayData(
    data: NewAlbumTopScreenUiData,
    onEvent: (NewAlbumTopScreenEvents) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(STANDARD_SCREEN_PADDING),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val artist = remember { mutableStateOf("") }
            val album = remember { mutableStateOf("") }
            val imageUrl = remember { mutableStateOf("") }
            var artistDropDownExpanded by remember { mutableStateOf(false) }
            val artistList = remember { mutableStateOf(
                listOf(
                    "Artist 1", "Artist 2", "Artist 3", "Artist 4", "Artist 5", "Artist 6", "Artist 7", "Artist 8", "Artist 9", "Artist 10", "Artist 11", "Artist 12", "Artist 13", "Artist 14", "Artist 15", "Artist 16", "Artist 17", "Artist 18", "Artist 19", "Artist 20"
                )
            ) }

            MyOutlinedInputText(
                label = "Artist",
                currentValue = artist.value,
                onValueChange = { artist.value = it }
            )
            TextButton(
                onClick = { artistDropDownExpanded = true }
            ) {
                Text(text = "Select From Existing Artists")
            }
            MyDropdownMenu(
                expanded = artistDropDownExpanded,
                options = artistList.value,
                onOptionSelected = { artist.value = it },
                onDismissRequest = { artistDropDownExpanded = it },
            )
            MyOutlinedInputText(
                label = "Album",
                currentValue = album.value,
                onValueChange = { album.value = it },
                modifier = Modifier.padding(top = 8.dp)
            )
            MyOutlinedInputText(
                label = "Image URL",
                currentValue = imageUrl.value,
                onValueChange = { imageUrl.value = it },
                modifier = Modifier.padding(top = 8.dp)
            )
            Box(
                modifier = Modifier.fillMaxWidth(0.5f)
            ){
                MyInternetImage(
                    imageUrl = imageUrl.value
                )
            }

            Text(text = data.someData.orEmpty())
            Button(
                onClick = {
                    onEvent(NewAlbumTopScreenEvents.ButtonClicked)
                }
            ) {
                Text(text = "Click Me")
            }
        }
    }
}





@Preview
@Composable
private fun PreviewDisplayData(){
    PreviewTheme {
        DetermineDisplayMode(
            uiState = NewAlbumTopScreenUiState(
               data = NewAlbumTopScreenUiData(
                   someData = "Hello"
               ),
            )
        )
    }
}

@Preview
@Composable
private fun PreviewDisplayNoData(){
    PreviewTheme {
        DetermineDisplayMode(
            uiState = NewAlbumTopScreenUiState(
                data = null,
            )
        )
    }
}
