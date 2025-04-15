package com.wmccd.whatgoeson.presentation.screens.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.MediaType
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.presentation.screens.common.STANDARD_SCREEN_PADDING
import com.wmccd.whatgoeson.presentation.screens.common.composables.ExternalArtistDestinationRow
import com.wmccd.whatgoeson.repository.database.AlbumArtistCount
import com.wmccd.whatgoeson.repository.database.MEDIA_TYPE
import com.wmccd.whatgoeson.utility.musicPlayer.MusicPlayer
import java.util.UUID

@Composable
fun StatsScreen(
    navController: NavHostController,
    viewModel: StatsViewModel = StatsViewModel()
) {

    // Listen for navigation events sent by the ViewModel
    LaunchedEffect(key1 = UUID.randomUUID().toString()) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToNextScreen -> {
                    //TODO Add Destination Route
//                    navController.navigate(NavigationEnum.AddDestination.route)
                }
            }
        }
    }
    DisplayContentMode(viewModel)
}

@Composable
private fun DisplayContentMode(viewModel: StatsViewModel) {
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
    uiState: StatsUiState,
    onEvent: (StatsEvents) -> Unit = {},
) {
    if (uiState.data == null) {
        DisplayError(stringResource(R.string.no_data_to_display))
    } else {
        DisplayData(uiState.data, onEvent)
    }
}

@Composable
fun DisplayData(
    data: StatsUiData,
    onEvent: (StatsEvents) -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(STANDARD_SCREEN_PADDING),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.album_count),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = data.albumCount.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.artist_count),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = data.artistCount.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            MediaTypeRow(data = data)
            Spacer(modifier = Modifier.height(32.dp))
            LazyColumn{
                items(data.artistAlbumCount.size){
                    ArtistRow(data, onEvent, it)
                }
            }
        }
    }
}

@Composable
private fun MediaTypeRow(
    data: StatsUiData
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MediaTypeItem(title = MediaType.CASSETTE.label, count = data.cassetteCount)
        MediaTypeItem(title = MediaType.DIGITAL.label, count = data.digitalCount)
        MediaTypeItem(title = MediaType.OPTICAL.label, count = data.opticalCount)
        MediaTypeItem(title = MediaType.VINYL.label, count = data.vinylCount)
    }
}

@Composable
private fun MediaTypeItem(
    title: String,
    count: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ArtistRow(
    data: StatsUiData,
    onEvent: (StatsEvents) -> Unit = {},
    it: Int
) {
    val externalDestinationRowDisplayed = remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable {
                externalDestinationRowDisplayed.value = !externalDestinationRowDisplayed.value
            },
        ) {
            Text(
                text = data.artistAlbumCount[it].artistName,
                modifier = Modifier.weight(0.8f),
            )
            Text(
                text = data.artistAlbumCount[it].albumCount.toString(),
                modifier = Modifier.weight(0.2f),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.primary
            )
        }
        AnimatedVisibility(externalDestinationRowDisplayed.value) {
            Row{
                ExternalArtistDestinationRow(
                    artistName = data.artistAlbumCount[it].artistName,
                    spotifyEnabled = data.spotifyInstalled,
                    youTubeMusicEnabled = data.youTubeMusicInstalled,
                    onSpotifyTapped = {
                        onEvent(
                            StatsEvents.MusicPlayerTapped(
                                artistName = data.artistAlbumCount[it].artistName,
                                musicPlayer = MusicPlayer.SPOTIFY
                            )
                        )
                    },
                    onYouTubeMusicTapped = {
                        onEvent(
                            StatsEvents.MusicPlayerTapped(
                                artistName = data.artistAlbumCount[it].artistName,
                                musicPlayer = MusicPlayer.YOUTUBE_MUSIC
                            )
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDisplayData() {
    PreviewTheme {
        DisplayContent(
            uiState = StatsUiState(
                data = StatsUiData(
                   albumCount = 2,
                    artistCount = 3,
                    artistAlbumCount = listOf(
                        AlbumArtistCount("Artist 1", 1),
                        AlbumArtistCount("Artist 2", 2),
                        AlbumArtistCount("Artist 3", 3),
                        AlbumArtistCount("Artist 4", 2),
                        AlbumArtistCount("Artist 5", 3),
                    )
                ),
            )
        )
    }
}