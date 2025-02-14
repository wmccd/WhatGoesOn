package com.wmccd.whatgoeson.presentation.screens.albumList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.wmccd.whatgoeson.presentation.screens.common.NoAlbums
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.presentation.screens.common.STANDARD_SCREEN_PADDING
import com.wmccd.whatgoeson.presentation.screens.common.composables.MyInternetImage
import com.wmccd.whatgoeson.repository.database.AlbumWithArtistName
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
    if (data.albumList.isNullOrEmpty()) {
        DisplayNoAlbums()

    }else {
        AlbumList(
            albumList = data.albumList,
            onEvent = onEvent,
            displayDeleteDialog = data.displayDeleteDialog,
            albumSelectedForDelete = data.albumSelectedForDelete
        )
    }
}

@Composable
private fun DisplayNoAlbums() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(STANDARD_SCREEN_PADDING),
        contentAlignment = Alignment.Center
    ) {
        NoAlbums()
    }
}

@Composable
fun AlbumList(
    albumList: List<AlbumWithArtistName>,
    onEvent: (AlbumListEvents) -> Unit,
    displayDeleteDialog: Boolean,
    albumSelectedForDelete: AlbumWithArtistName? = null
){
    LazyColumn {
        items(albumList.size) { index ->
            AlbumItem(
                album = albumList[index],
                onEvent = onEvent,

            )
            HorizontalDivider()
        }
    }
    if(displayDeleteDialog && albumSelectedForDelete != null){
        CheckBeforeDeleting(
            album = albumSelectedForDelete,
            onEvent = onEvent,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumItem(
    album: AlbumWithArtistName,
    onEvent: (AlbumListEvents) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().combinedClickable(
            onClick = {},
            onLongClick = {
                onEvent(AlbumListEvents.LongClicked(true, album))
            },
            onDoubleClick = {
                onEvent(AlbumListEvents.LongClicked(true, album))
            }
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MyInternetImage(
            imageUrl = album.albumUrl,
            modifier = Modifier.padding(horizontal = 8.dp).height(75.dp).width(75.dp)
        )
        Column(
            modifier = Modifier.weight(1f),

        ) {
            Text(
                text = album.albumName,
            )
            Text(
                text = album.artistName,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun CheckBeforeDeleting(
    album: AlbumWithArtistName,
    onEvent: (AlbumListEvents) -> Unit
){
    AlertDialog(
        onDismissRequest = {
            onEvent(AlbumListEvents.LongClicked(false, null))
        },
        title = {
            Text(text = stringResource(R.string.are_you_sure))
        },
        text = {
            Text(text = stringResource(R.string.this_action_cant_be_undone))
        },
        confirmButton = {
            //Displays the "Delete" button to confirm the action
            Button(onClick = {
                onEvent(AlbumListEvents.LongClicked(false, null))
                onEvent(AlbumListEvents.DeleteAlbum(album))

            }) {
                Text(text = stringResource(R.string.carry_on))
            }
        },
        dismissButton = {
            //Displays the "Cancel" button to cancel the action
            Button(onClick = {
                onEvent(AlbumListEvents.LongClicked(false, null))
            }) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}


@Preview
@Composable
private fun PreviewDisplayNoAlbums() {
    PreviewTheme {
        DisplayContent(
            uiState = AlbumListUiState(
                data = AlbumListUiData(
                    albumList = emptyList()
                ),
            )
        )
    }
}

@Preview
@Composable
private fun PreviewDisplayAlbums() {
    PreviewTheme {
        DisplayContent(
            uiState = AlbumListUiState(
                data = AlbumListUiData(
                    albumList = listOf(
                        AlbumWithArtistName(
                            albumId = 1,
                            albumName = "Album 1",
                            albumUrl = "",
                            artistName = "Artist 1",
                            artistId = 1,
                        ),
                    )
                )
            )
        )
    }
}