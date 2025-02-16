package com.wmccd.whatgoeson.presentation.screens.newAlbum

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.MyApplication
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
fun NewAlbumScreen(
    navController: NavHostController,
    viewModel: NewAdditionScreenViewModel = NewAdditionScreenViewModel()
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
private fun DisplayContent(viewModel: NewAdditionScreenViewModel) {
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
    uiState: NewAlbumScreenUiState,
    onEvent: (NewAlbumScreenEvents) -> Unit = {},
) {
    if(uiState.data == null) {
        DisplayError(stringResource(R.string.no_data_to_display))
    } else {
        DisplayData(uiState.data, onEvent)
    }
}

@Composable
private fun DisplayData(
    data: NewAlbumScreenUiData,
    onEvent: (NewAlbumScreenEvents) -> Unit
) {
    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(STANDARD_SCREEN_PADDING),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var artistDropDownExpanded by remember { mutableStateOf(false) }
            MyOutlinedInputText(
                label = stringResource(R.string.artist),
                currentValue = data.artistName.orEmpty(),
                onValueChange = {
                    onEvent(
                        NewAlbumScreenEvents.ArtistNameChanged(it)
                    )
                }
            )
            TextButton(
                onClick = { artistDropDownExpanded = true }
            ) {
                Text(text = stringResource(R.string.select_from_existing_artists))
            }
            MyDropdownMenu(
                expanded = artistDropDownExpanded,
                options = data.artistIdNameList,
                onOptionSelected = { id, name ->
                    onEvent(
                        NewAlbumScreenEvents.ArtistSelected(id, name)
                    )
               },
                onDismissRequest = { artistDropDownExpanded = it },
            )
            MyOutlinedInputText(
                label = "Album",
                currentValue = data.albumName.orEmpty(),
                onValueChange = {
                    onEvent(
                        NewAlbumScreenEvents.AlbumNameChanged(it)
                    )
                },
                modifier = Modifier.padding(top = 8.dp)
            )
            MyOutlinedInputText(
                label = "Image URL",
                currentValue = data.imageUrl.orEmpty(),
                onValueChange = {
                    onEvent(NewAlbumScreenEvents.AlbumImageUrlChanged(it))
                },
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            var imageSize by remember { mutableIntStateOf(20) }
            MyInternetImage(
                imageUrl = data.imageUrl.orEmpty(),
                successful = { successful ->
                    MyApplication.utilities.logger.log(
                        Log.INFO,
                        "TAG",
                        "fetch image successful: $successful"
                    )
                    if (successful) {
                        imageSize = 300
                    } else {
                        imageSize = 20
                    }
                },
                imageSize = imageSize,
            )
            Button(
                enabled = data.saveButtonEnabled,
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    onEvent(NewAlbumScreenEvents.SaveButtonClicked)
                }
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}





@Preview
@Composable
private fun PreviewDisplayData(){
    PreviewTheme {
        DetermineDisplayMode(
            uiState = NewAlbumScreenUiState(
               data = NewAlbumScreenUiData(
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
            uiState = NewAlbumScreenUiState(
                data = null,
            )
        )
    }
}
