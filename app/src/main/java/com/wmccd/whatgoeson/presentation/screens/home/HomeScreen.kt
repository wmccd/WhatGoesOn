package com.wmccd.whatgoeson.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.NavigationEnum
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.screens.NoAlbums
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.presentation.screens.common.STANDARD_SCREEN_PADDING
import com.wmccd.whatgoeson.presentation.screens.common.composables.ExternalDestinationRow
import com.wmccd.whatgoeson.presentation.screens.common.composables.MyInternetImage
import com.wmccd.whatgoeson.utility.musicPlayer.MusicPlayer
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
                    navController.navigate(NavigationEnum.NewAlbumScreen.route)
                }
            }
        }
    }
    DetermineDisplayMode(viewModel)
}

@Composable
private fun DetermineDisplayMode(viewModel: HomeViewModel) {
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
private fun DisplayContent(
    uiState: HomeUiState,
    onEvent: (HomeEvents) -> Unit = {}
) {
    if(uiState.data == null) {
        DisplayError(stringResource(R.string.no_data_to_display))
    }else {
        DisplayData(
            data = uiState.data,
            albumFavouriteFilter = uiState.data.albumFilterSort,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun DisplayData(
    data: HomeUiData?,
    albumFavouriteFilter: AlbumFavouriteFilter,
    onEvent: (HomeEvents) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = STANDARD_SCREEN_PADDING),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            when{
                data?.noAlbumsStored == true -> NoAlbums()
                else -> AlbumDetails(
                    data = data,
                    albumFavouriteFilter = albumFavouriteFilter,
                    onEvent = onEvent,
                    noFilterMatches = data?.noFilterMatches == true
                )
            }
        }
    }
}



@Composable
private fun AlbumDetails(
    data: HomeUiData?,
    albumFavouriteFilter: AlbumFavouriteFilter,
    onEvent: (HomeEvents) -> Unit = {},
    noFilterMatches: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StickyFilters(
            albumFavouriteFilter = albumFavouriteFilter,
            onEvent = onEvent
        )
        Column(
            modifier = Modifier.fillMaxSize().weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (noFilterMatches) {
                NoFilterMatches()
            }else {
                FilterMatches(data, onEvent)
            }
        }
        if(!noFilterMatches && data != null) {
            AnimatedVisibility(data.externalDestinationEnabled) {
                ExternalDestinationRow(
                    albumName = data.albumName ?: "",
                    artistName = data.artistName ?: "",
                    spotifyEnabled = MyApplication.device.spotifyInstalled,
                    youTubeMusicEnabled = MyApplication.device.youTubeMusicInstalled,
                    onSpotifyTapped = {
                        onEvent(
                            HomeEvents.MusicPlayerTapped(
                                albumName = data.albumName ?: "",
                                artistName = data.artistName ?: "",
                                musicPlayer = MusicPlayer.SPOTIFY
                            )
                        )
                    },
                    onYouTubeMusicTapped = {
                        onEvent(
                            HomeEvents.MusicPlayerTapped(
                                albumName = data.albumName ?: "",
                                artistName = data.artistName ?: "",
                                musicPlayer = MusicPlayer.YOUTUBE_MUSIC
                            )
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



@Composable
fun NoFilterMatches() {
    Text(
        text = stringResource(R.string.no_filter_matches),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ColumnScope.FilterMatches(
    data: HomeUiData?,
    onEvent: (HomeEvents) -> Unit = {},
    ) {
    val fetchedImageFor = remember { mutableStateOf("") }
    val fetchedImageSuccessful = remember { mutableStateOf(true) }

    if(fetchedImageFor.value != data?.albumName){
        fetchedImageSuccessful.value = true
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .clickable {
            onEvent(HomeEvents.AlbumTapped)
        },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${data?.albumName}",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "${data?.artistName}",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(fetchedImageSuccessful.value) {
            MyInternetImage(
                imageUrl = data?.albumArtUrl.orEmpty(),
                successful = {
                    fetchedImageSuccessful.value = it
                    fetchedImageFor.value = data?.albumName.toString()
                }
            )
        }
    }
}

@Composable
private fun StickyFilters(
    albumFavouriteFilter: AlbumFavouriteFilter,
    onEvent: (HomeEvents) -> Unit = {}
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
                selected = albumFavouriteFilter == AlbumFavouriteFilter.ALL_ALBUMS,
                onClick = {
                    onEvent(HomeEvents.AlbumFilterSortClicked(AlbumFavouriteFilter.ALL_ALBUMS))
                },
                label = {
                    Text(
                        text = stringResource(R.string.all),
                        style = TextStyle(fontSize = nonScaledFontSize)
                    )
                },
            )
            FilterChip(
                selected = albumFavouriteFilter == AlbumFavouriteFilter.FAVOURITES_ONLY,
                onClick = {
                    onEvent(HomeEvents.AlbumFilterSortClicked(AlbumFavouriteFilter.FAVOURITES_ONLY))
                },
                label = {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = stringResource(R.string.favourites),
                        tint = Color.Red
                    )
                }
            )
            FilterChip(
                selected = albumFavouriteFilter == AlbumFavouriteFilter.NON_FAVOURITES_ONLY,
                onClick = {
                    onEvent(HomeEvents.AlbumFilterSortClicked(AlbumFavouriteFilter.NON_FAVOURITES_ONLY))
                },
                label = {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.favourites),
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDisplayData(){
    PreviewTheme {
        DisplayContent(
            uiState = HomeUiState(
                data = HomeUiData(
                    artistName = "Artist",
                    albumName = "Album",
                    albumArtUrl = null,
                ),
            )
        )
    }
}

@Preview
@Composable
private fun PreviewDisplayNoAlbums(){
    PreviewTheme {
        DisplayContent(
            uiState = HomeUiState(
                data = HomeUiData(
                    noAlbumsStored = true
                ),
            )
        )
    }
}

@Preview
@Composable
private fun PreviewDisplayNoFilterMatches(){
    PreviewTheme {
        DisplayContent(
            uiState = HomeUiState(
                data = HomeUiData(
                    noFilterMatches = true
                ),
            )
        )
    }
}

@Preview
@Composable
private fun PreviewDisplayMatches(){
    PreviewTheme {
        DisplayContent(
            uiState = HomeUiState(
                data = HomeUiData(
                    artistName = "Artist",
                    albumName = "Album",
                    albumArtUrl = null,
                ),
            )
        )
    }
}
