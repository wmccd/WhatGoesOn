package com.wmccd.whatgoeson.presentation.screens.albumList

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.MediaType
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.presentation.screens.common.STANDARD_SCREEN_PADDING
import com.wmccd.whatgoeson.presentation.screens.common.composables.ExternalAlbumDestinationRow
import com.wmccd.whatgoeson.presentation.screens.common.composables.INTERNET_IMAGE_NOT_AVAILABLE
import com.wmccd.whatgoeson.presentation.screens.common.composables.MyInternetImage
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.screens.NoAlbums
import com.wmccd.whatgoeson.repository.database.AlbumWithArtistName
import com.wmccd.whatgoeson.utility.musicPlayer.MusicPlayer
import kotlinx.coroutines.launch
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
            data = data,
            onEvent = onEvent,
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
    data: AlbumListUiData,
    onEvent: (AlbumListEvents) -> Unit,
){
    Column {
        StickyFilters(
            albumSort = data.albumSort,
            onEvent = onEvent
        )
        DisplayAlbums(
            data = data,
            onEvent = onEvent,
        )
        if (data.displayDeleteDialog && data.albumSelectedForDelete != null) {
            CheckBeforeDeleting(
                album = data.albumSelectedForDelete,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun DisplayAlbums(
    data: AlbumListUiData,
    onEvent: (AlbumListEvents) -> Unit,
) {
    var scrollLetter by remember {mutableStateOf(data.filterChar)}

    Row(
        modifier = Modifier.fillMaxWidth()
    ){
        Column(
            modifier = Modifier
                .weight(.1f)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayFilterLetters(
                onEvent = {
                    scrollLetter = it
                },
                appliedFilterChar = data.filterChar
            )
        }
        VerticalDivider(modifier = Modifier.padding(horizontal = 8.dp))
        Box(modifier = Modifier.weight(.9f)) {
            DisplayAlbumList(
                data = data,
                onEvent = onEvent,
                scrollLetter = scrollLetter
            )
        }
    }
}


@Composable
private fun DisplayFilterLetters(
    onEvent: (Char) -> Unit,
    appliedFilterChar: Char? = null
) {
    val filterList = ('A'..'Z').map { it }.toList()
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(filterList.size) { index ->
            DisplayFilterLetter(
                onEvent = onEvent,
                c = filterList[index],
                appliedFilterChar = appliedFilterChar
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun DisplayFilterLetter(
    onEvent: (Char) -> Unit,
    c: Char,
    appliedFilterChar: Char? = null
) {
    Text(
        text = c.toString(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .background(
                if (c == appliedFilterChar)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.background
            )
            .clickable {
                onEvent(c)
            }
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun DisplayAlbumList(
    data: AlbumListUiData,
    onEvent: (AlbumListEvents) -> Unit,
    scrollLetter: Char? = null
) {
    val albumList = data.albumList.orEmpty()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    if(scrollLetter!=null) {
        val index = if (data.albumSort == AlbumSort.AZ_ALBUMS)
            albumList.indexOfFirst { it.albumName.startsWith(scrollLetter, ignoreCase = true) }
        else
            albumList.indexOfFirst { it.artistName.startsWith(scrollLetter, ignoreCase = true) }
        if (index != -1) {
            coroutineScope.launch {
                listState.scrollToItem(index)
            }
        }
    }

    LazyColumn(
        state = listState,
    ) {
        items(albumList.size) { index ->
            AlbumItem(
                album = albumList[index],
                spotifyEnabled = data.spotifyInstalled,
                youTubeMusicEnabled = data.youTubeMusicInstalled,
                onEvent = onEvent,
                expandedDisplay = data.albumSelectedForDetails == albumList[index].albumId
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun StickyFilters(
    albumSort: AlbumSort,
    onEvent: (AlbumListEvents) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .zIndex(1f),
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
                selected = albumSort == AlbumSort.AZ_ALBUMS,
                onClick = {
                    onEvent(AlbumListEvents.SortOrderClicked(AlbumSort.AZ_ALBUMS))
                },
                label = {
                    Text(
                        text = stringResource(R.string.az_albums),
                        style = TextStyle(fontSize = nonScaledFontSize)
                    )
                },
            )
            FilterChip(
                selected = albumSort == AlbumSort.AZ_ARTISTS,
                onClick = {
                    onEvent(AlbumListEvents.SortOrderClicked(AlbumSort.AZ_ARTISTS))
                },
                label = {
                    Text(
                        text = stringResource(R.string.az_artists),
                        style = TextStyle(fontSize = nonScaledFontSize)
                    )
                }
            )
            FilterChip(
                selected = albumSort == AlbumSort.FAVOURITES,
                onClick = {
                    onEvent(AlbumListEvents.SortOrderClicked(AlbumSort.FAVOURITES))
                },
                label = {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = stringResource(R.string.favourites),
                        tint = Color.Red
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumItem(
    album: AlbumWithArtistName,
    spotifyEnabled: Boolean,
    youTubeMusicEnabled: Boolean,
    onEvent: (AlbumListEvents) -> Unit,
    expandedDisplay: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onEvent(AlbumListEvents.AlbumClicked(album.albumId))
                    },
                    onLongClick = {
                        onEvent(AlbumListEvents.AlbumLongClicked(true, album))
                    },
                    onDoubleClick = {}
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (album.albumUrl == INTERNET_IMAGE_NOT_AVAILABLE)
                Box(
                    modifier = Modifier
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
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
            ) {

                Text(
                    text = album.albumName,
                )
                Text(
                    text = album.artistName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
            FavouriteIcon(onEvent, album)
        }
        AnimatedVisibility(expandedDisplay){
            ExternalAlbumDestinationRow(
                albumName = album.albumName,
                artistName = album.artistName,
                spotifyEnabled = spotifyEnabled,
                youTubeMusicEnabled = youTubeMusicEnabled,
                onSpotifyTapped = {
                    onEvent(AlbumListEvents.MusicPlayerTapped(album.albumName, album.artistName, MusicPlayer.SPOTIFY))
                },
                onYouTubeMusicTapped = {
                    onEvent(AlbumListEvents.MusicPlayerTapped(album.albumName, album.artistName, MusicPlayer.YOUTUBE_MUSIC))
                }
            )
        }
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
            onEvent(AlbumListEvents.AlbumLongClicked(false, null))
        },
        title = {
            Text(text = stringResource(R.string.are_you_sure))
        },
        text = {
            Text(text = stringResource(R.string.you_can_add_again_later))
        },
        confirmButton = {
            //Displays the "Delete" button to confirm the action
            Button(onClick = {
                onEvent(AlbumListEvents.AlbumLongClicked(false, null))
                onEvent(AlbumListEvents.DeleteAlbum(album))

            }) {
                Text(text = stringResource(R.string.carry_on))
            }
        },
        dismissButton = {
            //Displays the "Cancel" button to cancel the action
            Button(onClick = {
                onEvent(AlbumListEvents.AlbumLongClicked(false, null))
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
                            mediaType = MediaType.VINYL,
                            artistName = "Artist 1",
                            artistId = 1,
                            albumFavourite = false,
                        ),
                        AlbumWithArtistName(
                            albumId = 1,
                            albumName = "Album 2",
                            albumUrl = "",
                            mediaType = MediaType.DIGITAL,
                            artistName = "Artist 2",
                            artistId = 1,
                            albumFavourite = true,
                        ),
                    )
                )
            )
        )
    }
}

