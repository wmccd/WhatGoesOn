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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.NavigationEnum
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.presentation.screens.common.STANDARD_SCREEN_PADDING
import com.wmccd.whatgoeson.presentation.screens.common.composables.ExternalAlbumDestinationRow
import com.wmccd.whatgoeson.presentation.screens.common.composables.MyInternetImage
import com.wmccd.whatgoeson.presentation.screens.common.composables.SectionLabel
import com.wmccd.whatgoeson.presentation.screens.common.composables.SectionTitle
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.screens.NoAlbums
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels.Album
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels.AlbumInformationModel
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels.Background
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels.Musicians
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels.Reception
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels.RecordingDetails
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels.Sources
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels.Tracks
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
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            when{
                data?.noAlbumsStored == true -> NoAlbums()
                data?.showOverlay == true-> DisplayOverLay(
                    data = data,
                    onEvent = onEvent
                )
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
fun DisplayOverLay(
    data: HomeUiData?,
    onEvent: (HomeEvents) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = STANDARD_SCREEN_PADDING)
            .verticalScroll(scrollState)
            .clickable {
                onEvent(
                    HomeEvents.CloseOverlay
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        data?.let{
            when{
                data.showSimilarAlbums -> DisplayRecommendations(data)
                data.showAlbumInformation -> DisplayAlbumInformation(data)
            }
        }
    }
}

@Composable
private fun DisplayRecommendations(data: HomeUiData) {

    if(data.similarAlbums.isEmpty()){
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.searching_the_known_universe_for_you))
    } else {
        data.similarAlbums.forEach {
            Text(
                text = it.album_name,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = it.artist,
                fontWeight = FontWeight.Bold
            )
            Text(it.release_year)
            Text(it.details)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
private fun DisplayAlbumInformation(data: HomeUiData) {

    val album = data.albumInformation?.album
    if(album == null){
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.consulting_the_oracle_for_you))
    } else {
        AlbumInformationMain(album)
        AlbumInformationCredits(album)
        AlbumInformationTracks(album)
        AlbumInformationPlayers(album)
        AlbumInformationReception(album)
        AlbumInformationBackground(album)
        AlbumInformationRecording(album)
    }
}

@Composable
private fun AlbumInformationRecording(album: Album) {
    SectionTitle(stringResource(R.string.recording))
    SectionLabel(stringResource(R.string.dates))
    Text(text = album.recordingDetails?.dates.orEmpty(), modifier = Modifier.fillMaxWidth())
    SectionLabel(stringResource(R.string.location))
    Text(text = album.recordingDetails?.location.orEmpty(), modifier = Modifier.fillMaxWidth())
    SectionLabel(stringResource(R.string.notes))
    Text(text = album.recordingDetails?.notes.orEmpty(), modifier = Modifier.fillMaxWidth())
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun AlbumInformationBackground(album: Album) {
    SectionTitle(stringResource(R.string.background))
    SectionLabel(stringResource(R.string.context))
    Text(text = album.background?.context.orEmpty(),)
    SectionLabel(stringResource(R.string.influence))
    Text(text = album.background?.influence.orEmpty(),)
    SectionLabel(stringResource(R.string.also))
    Text(text = album.background?.interestingAnecdote.orEmpty(),)
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun AlbumInformationReception(album: Album) {
    SectionTitle(stringResource(R.string.reception))
    Text(
        text = album.reception?.overall.orEmpty(),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
    )
    album.reception?.sources?.forEach { source ->
        val sourcesAndRating = source.source + if (source.rating.isNullOrEmpty()) "" else " - ${source.rating}"
        SectionLabel(sourcesAndRating)
        Text(
            text = "\"${source.reviewSnippet}\"",
            fontStyle = FontStyle.Italic
        )
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun AlbumInformationPlayers(album: Album) {
    SectionTitle(stringResource(R.string.players))
    album.musicians.forEach { musician ->
        SectionLabel("${musician.name}")
        Text(
            text = musician.instruments.toString().drop(1).dropLast(1),
            modifier = Modifier.fillMaxWidth()
        )
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun AlbumInformationTracks(album: Album) {
    SectionTitle(stringResource(R.string.tracks))
    album.tracks.forEachIndexed { index, track ->
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            text = "${index + 1}. ${track.title} - (${track.duration})"
        )
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}



@Composable
private fun AlbumInformationMain(album: Album) {
    Text(
        text = album.title.orEmpty(),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = album.artist.orEmpty(),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Released: ${album.releaseDate.orEmpty()}",
        textAlign = TextAlign.Center
    )

    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun AlbumInformationCredits(album: Album) {
    SectionLabel(stringResource(R.string.label))
    Text(album.label.orEmpty(), modifier = Modifier.fillMaxWidth())
    SectionLabel(stringResource(R.string.producer_s))
    Text(album.producer.orEmpty(), modifier = Modifier.fillMaxWidth())
    SectionLabel(stringResource(R.string.genre_s))
    Text(album.genre.toString().drop(1).dropLast(1), modifier = Modifier.fillMaxWidth())
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when{
                noFilterMatches ->NoFilterMatches()
                else -> FrontCard(data, onEvent)
            }
        }
        DisplayExternalRow(
            display = noFilterMatches,
            data =data,
            onEvent = onEvent
        )
    }
}

@Composable
private fun ColumnScope.DisplayExternalRow(
    display: Boolean,
    data: HomeUiData?,
    onEvent: (HomeEvents) -> Unit
) {
    if (!display && data != null) {
        AnimatedVisibility(data.externalDestinationEnabled) {
            ExternalAlbumDestinationRow(
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


@Composable
fun NoFilterMatches() {
    Text(
        text = stringResource(R.string.no_filter_matches),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ColumnScope.FrontCard(
    data: HomeUiData?,
    onEvent: (HomeEvents) -> Unit = {},
    ) {
    val fetchedImageFor = remember { mutableStateOf("") }
    val fetchedImageSuccessful = remember { mutableStateOf(true) }

    if(fetchedImageFor.value != data?.albumName){
        fetchedImageSuccessful.value = true
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
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
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
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
            DisplayAiIconsRow(
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun DisplayAiIconsRow(
    onEvent: (HomeEvents) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    onEvent(HomeEvents.InformationTapped)
                },
        )
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    onEvent(HomeEvents.SearchTapped)
                },
        )
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

@Preview
@Composable
private fun PreviewAlbumInformation(){
    PreviewTheme {
        DisplayOverLay(
            data = HomeUiData(
                artistName = "Artist",
                albumName = "Album",
                albumArtUrl = null,
                showSimilarAlbums = false,
                showAlbumInformation = true,
                albumInformation = AlbumInformationModel(
                    album = Album(
                        title = "Album Title",
                        artist = "Artist Name",
                        releaseDate = "Release Date",
                        producer = "Producer Name",
                        genre = arrayListOf("Genre 1", "Genre 2"),
                        tracks = arrayListOf(
                            Tracks(
                                title = "Track Title",
                                duration = "3:43"
                            ),
                            Tracks(
                                title = "Track Title",
                                duration = "2:30"
                            )
                        ),
                        musicians = arrayListOf(
                            Musicians(
                                name = "Musician Name",
                                instruments = arrayListOf("Instrument 1", "Instrument 2")
                            ),
                            Musicians(
                                name = "Musician Name",
                                instruments = arrayListOf("Instrument 1", "Instrument 2")
                            )
                        ),
                        reception = Reception(
                            overall = "Overall Review Summary",
                            sources = arrayListOf(
                                Sources(
                                    source = "Review Source",
                                    rating = "4/5 or B+",
                                    reviewSnippet = "Description of artist's state of mind during recording"
                                ),
                                Sources(
                                    source = "Review Source",
                                    rating = "4/5 or B+",
                                    reviewSnippet = "Description of artist's state of mind during recording"
                                )
                            )
                        ),
                        background = Background(
                            context = "Background Context",
                            influence = "Influences",
                            interestingAnecdote = "Description of artist's state of mind during recording"
                        ),
                        recordingDetails = RecordingDetails(
                            location = "Recording Location",
                            dates = "Recording Dates",
                            notes = "Recording Notes"
                        )
                    )
                )
            ),
        )
    }
}
