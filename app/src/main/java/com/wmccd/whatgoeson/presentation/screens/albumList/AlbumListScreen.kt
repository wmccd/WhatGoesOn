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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.NoAlbums
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.presentation.screens.common.STANDARD_SCREEN_PADDING
import com.wmccd.whatgoeson.presentation.screens.common.composables.INTERNET_IMAGE_NOT_AVAILABLE
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
            albumSelectedForDelete = data.albumSelectedForDelete,
            albumFilterSort = data.albumFilterSort
        )
    }
}

@Composable
private fun DisplayNoAlbums() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(STANDARD_SCREEN_PADDING),
        contentAlignment = Alignment.TopCenter
    ) {
        NoAlbums()
    }
}

@Composable
fun AlbumList(
    albumList: List<AlbumWithArtistName>,
    onEvent: (AlbumListEvents) -> Unit,
    displayDeleteDialog: Boolean,
    albumFilterSort: AlbumFilterSort,
    albumSelectedForDelete: AlbumWithArtistName? = null,
){
    Column {
        StickyFilters(
            albumFilterSort = albumFilterSort,
            onEvent = onEvent
        )
        DisplayAlbums(
            albumList,
            onEvent,
            albumFilterSort
        )
        if (displayDeleteDialog && albumSelectedForDelete != null) {
            CheckBeforeDeleting(
                album = albumSelectedForDelete,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun DisplayAlbums(
    albumList: List<AlbumWithArtistName>,
    onEvent: (AlbumListEvents) -> Unit,
    albumFilterSort: AlbumFilterSort,
) {
    val newAlbumList = when(albumFilterSort){
        AlbumFilterSort.AZ_ALBUMS -> albumList
            .sortedBy { it.albumName }
        AlbumFilterSort.AZ_ARTISTS -> albumList
            .sortedWith(
                compareBy({ it.artistName.lowercase() }, { it.albumName })
            )
        AlbumFilterSort.FAVOURITES -> albumList
            .filter { it.albumFavourite }
            .sortedBy { it.albumName }
            .sortedBy { it.artistName.lowercase() }
    }
    LazyColumn {
        items(newAlbumList.size) { index ->
            AlbumItem(
                album = newAlbumList[index],
                onEvent = onEvent,

                )
            HorizontalDivider()
        }
    }
}

@Composable
private fun StickyFilters(
    albumFilterSort: AlbumFilterSort,
    onEvent: (AlbumListEvents) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .zIndex(1f), // Ensure it's drawn on top
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            val configuration = LocalConfiguration.current
            val density = LocalDensity.current
            val nonScaledFontSize = with(density) {
                (16 * (configuration.fontScale)).sp
            }
            FilterChip(
                selected = albumFilterSort == AlbumFilterSort.AZ_ALBUMS,
                onClick = {
                    onEvent(AlbumListEvents.AlbumFilterSortClicked(AlbumFilterSort.AZ_ALBUMS))
                },
                label = {
                    Text(
                        text = stringResource(R.string.az_albums),
                        style = TextStyle(fontSize = nonScaledFontSize)
                    )
                },
            )
            FilterChip(
                selected = albumFilterSort == AlbumFilterSort.AZ_ARTISTS,
                onClick = {
                    onEvent(AlbumListEvents.AlbumFilterSortClicked(AlbumFilterSort.AZ_ARTISTS))
                },
                label = {
                    Text(
                        text = stringResource(R.string.az_artists),
                        style = TextStyle(fontSize = nonScaledFontSize)
                    )
                }
            )
            FilterChip(
                selected = albumFilterSort == AlbumFilterSort.FAVOURITES,
                onClick = {
                    onEvent(AlbumListEvents.AlbumFilterSortClicked(AlbumFilterSort.FAVOURITES))
                },
                label = {
                    Text(
                        text = stringResource(R.string.favourites),
                        style = TextStyle(fontSize = nonScaledFontSize)
                    )
                }
            )
        }
    }
}

@Composable
fun TextUnit.toNonScalableTextUnit(): TextUnit {
    val density = LocalDensity.current
    return with(density) {
        this@toNonScalableTextUnit.toDp().value.sp
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlbumItem(
    album: AlbumWithArtistName,
    onEvent: (AlbumListEvents) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
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
        if(album.albumUrl == INTERNET_IMAGE_NOT_AVAILABLE)
            Box(modifier = Modifier
                .padding(horizontal = 8.dp)
                .height(75.dp)
                .width(75.dp)
            )
        else
            MyInternetImage(
                imageUrl = album.albumUrl,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(75.dp)
                    .width(75.dp)
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
        FavouriteIcon(onEvent, album)
    }
}

@Composable
private fun FavouriteIcon(
    onEvent: (AlbumListEvents) -> Unit,
    album: AlbumWithArtistName
) {
    IconButton(
        onClick = {
            onEvent(
                AlbumListEvents.MarkAsFavourite(
                    isFavourite = !album.albumFavourite,
                    album = album
                )
            )
        }
    ) {
        Icon(
            imageVector = if (album.albumFavourite)
                Icons.Default.Favorite
            else
                Icons.Default.FavoriteBorder,
            contentDescription = stringResource(R.string.favourite),
            tint = if (album.albumFavourite)
                Color.Red
            else
                MaterialTheme.colorScheme.onBackground,
        )
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
                            albumFavourite = false
                        ),
                        AlbumWithArtistName(
                            albumId = 1,
                            albumName = "Album 2",
                            albumUrl = "",
                            artistName = "Artist 2",
                            artistId = 1,
                            albumFavourite = true
                        ),
                    )
                )
            )
        )
    }
}