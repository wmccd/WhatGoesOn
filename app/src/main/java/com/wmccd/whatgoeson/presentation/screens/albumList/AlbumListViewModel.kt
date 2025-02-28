package com.wmccd.whatgoeson.presentation.screens.albumList

import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.repository.database.Album
import com.wmccd.whatgoeson.repository.database.AlbumWithArtistName
import com.wmccd.whatgoeson.utility.device.InstalledAppChecker
import com.wmccd.whatgoeson.utility.musicPlayer.Spotify
import com.wmccd.whatgoeson.utility.musicPlayer.YouTubeMusic
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class AlbumListViewModel(
    mockedUiStateForTestingAndPreviews: AlbumListUiState? = null,
) : ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(AlbumListUiState())
    val uiState: StateFlow<AlbumListUiState> = _uiState.asStateFlow()

    //keeps track of when we want to navigate to another screen
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val installedAppChecker = InstalledAppChecker()

    init {
        //The init block **only** runs when the ViewModel is created
        if (mockedUiStateForTestingAndPreviews == null)
            liveData()
        else
            mockedUiStateMode(mockedUiStateForTestingAndPreviews)
    }

    private fun mockedUiStateMode(uiStateForTestingAndPreviews: AlbumListUiState) {
        _uiState.value = uiStateForTestingAndPreviews
    }

    private fun liveData() {
        MyApplication.utilities.logger.log(Log.INFO, TAG, "fetching Live Data")
        //Update the state to indicate that we are fetching data
        _uiState.value = AlbumListUiState(isLoading = true)
        viewModelScope.launch {
            fetchData()
        }
    }


    private suspend fun fetchData() {
        //fetch the data and update the screen state
        viewModelScope.launch {
            try {
                fetchUiData()
            } catch (ex: Exception) {
                //something went wrong, show the error message
                MyApplication.utilities.logger.log(
                    Log.ERROR,
                    TAG,
                    "fetching Live Data: Exception",
                    ex
                )
                _uiState.value = uiState.value.copy(
                    error = ex.message
                )
            }
        }
    }

    private suspend fun fetchUiData() {
        MyApplication.utilities.logger.log(Log.INFO, TAG, "fetchUiData")
        MyApplication.repository.appDatabase.albumDao().getAllDetails().collect { allDetails ->
            val filterSortApplied = _uiState.value.data?.albumSort?: AlbumSort.AZ_ALBUMS
            MyApplication.utilities.logger.log(Log.INFO, TAG, "fetchUiData collecting $allDetails")
            _uiState.value = uiState.value.copy(
                isLoading = false,
                data = AlbumListUiData(
                    albumList = allDetails.sortedBy { it.albumName },
                    displayDeleteDialog = _uiState.value.data?.displayDeleteDialog ?: false,
                    albumSelectedForDelete = _uiState.value.data?.albumSelectedForDelete,
                    albumSort = filterSortApplied,
                    spotifyInstalled = installedAppChecker.check(InstalledAppChecker.AppPackage.SPOTIFY),
                    youTubeMusicInstalled = installedAppChecker.check(InstalledAppChecker.AppPackage.YOUTUBE_MUSIC)
                )
            )
        }
    }

    fun onEvent(event: AlbumListEvents) {
        //the user tapped on something on the screen and we need to handle that
        MyApplication.utilities.logger.log(Log.INFO, TAG, "onEvent $event")
        when (event) {
            AlbumListEvents.ButtonClicked -> onActionButtonClicked()
            is AlbumListEvents.DeleteAlbum -> onDeleteAlbum(event.album)
            is AlbumListEvents.AlbumLongClicked -> onAlbumLongClicked(event.clicked, album = event.album)
            is AlbumListEvents.MarkAsFavourite -> onMarkAsFavourite(event.isFavourite, event.album)
            is AlbumListEvents.SortOrderClicked -> onSortOrderClicked(event.albumSort)
            is AlbumListEvents.MusicPlayerTapped -> onMusicPlayerTapped(event.album, event.musicPlayer)
        }
    }

    private fun onMusicPlayerTapped(
        album: AlbumWithArtistName,
        musicPlayer: MusicPlayer
    ) {
        when(musicPlayer){
            MusicPlayer.SPOTIFY -> Spotify().open(album.artistName, album.albumName)
            MusicPlayer.YOUTUBE_MUSIC -> YouTubeMusic().open(album.artistName, album.albumName)
        }
    }

    private fun onSortOrderClicked(albumSort: AlbumSort) {
        val newListOrder = when(albumSort) {
            AlbumSort.AZ_ALBUMS -> sortedByAlbum()
            AlbumSort.AZ_ARTISTS -> sortedByArtistThenArtist()
            AlbumSort.FAVOURITES -> sortedByFavouriteThenArtist()
        }
        _uiState.value = uiState.value.copy(
            data = uiState.value.data?.copy(
                albumSort = albumSort,
                albumList = newListOrder
            )
        )
    }

    private fun sortedByFavouriteThenArtist() =
        _uiState.value.data?.albumList
            ?.sortedBy { it.artistName.lowercase() }
            ?.sortedByDescending { it.albumFavourite }


    private fun sortedByArtistThenArtist() = _uiState.value.data?.albumList
        ?.sortedWith(compareBy({ it.artistName.lowercase() }, { it.artistName })
    )

    private fun sortedByAlbum() = uiState.value.data?.albumList?.sortedBy { it.albumName }

    private fun onMarkAsFavourite(isFavourite: Boolean, album: AlbumWithArtistName) {
        viewModelScope.launch {
            MyApplication.repository.appDatabase.albumDao().update(
                Album(
                    id = album.albumId,
                    name = album.albumName,
                    imageUrl = album.albumUrl,
                    artistId = album.artistId,
                    isFavourite = isFavourite
                )
            )
        }
    }

    private fun onAlbumLongClicked(clicked: Boolean, album: AlbumWithArtistName? = null) {
        _uiState.value = uiState.value.copy(
            data = uiState.value.data?.copy(
                displayDeleteDialog = clicked,
                albumSelectedForDelete = album
            )
        )
    }

    private fun onDeleteAlbum(album: AlbumWithArtistName) {
        MyApplication.utilities.logger.log(Log.INFO, TAG, "onDeleteAlbum $album")
        viewModelScope.launch {
            MyApplication.repository.appDatabase.albumDao().delete(
                Album(
                    id =  album.albumId,
                    name = album.albumName,
                    imageUrl = album.albumUrl,
                    artistId = album.artistId
                )
            )
            fetchData()
        }
    }

    private fun onActionButtonClicked() {
        //the main action button was clicked and we want to move to next screen
        viewModelScope.launch {
            //_navigationEvent.emit(NavigationEvent.NavigateToNextScreen)
        }
    }

    companion object {
        private val TAG = AlbumListViewModel::class.java.simpleName
    }
}

data class AlbumListUiState(
    val isLoading: Boolean = false,
    val data: AlbumListUiData? = null,
    val error: String? = null
)

data class AlbumListUiData(
    val albumList: List<AlbumWithArtistName>? = null,
    val displayDeleteDialog: Boolean = false,
    val albumSelectedForDelete: AlbumWithArtistName? = null,
    val albumSort: AlbumSort = AlbumSort.AZ_ALBUMS,
    val filterChar: Char? = null,
    val chromeTabSearch: String? = null,
    val spotifyInstalled:Boolean = false,
    val youTubeMusicInstalled:Boolean = false
)

enum class AlbumSort {
    AZ_ALBUMS,
    AZ_ARTISTS,
    FAVOURITES
}

enum class MusicPlayer(@DrawableRes val imageId: Int) {
    SPOTIFY(R.drawable.logo_spotify),
    YOUTUBE_MUSIC(R.drawable.logo_youtubemusic)
}

sealed interface AlbumListEvents {
    data object ButtonClicked : AlbumListEvents
    data class AlbumLongClicked(val clicked: Boolean, val album: AlbumWithArtistName?) : AlbumListEvents
    data class DeleteAlbum(val album: AlbumWithArtistName) : AlbumListEvents
    data class MarkAsFavourite(val isFavourite: Boolean, val album: AlbumWithArtistName) : AlbumListEvents
    data class SortOrderClicked(val albumSort: AlbumSort) : AlbumListEvents
    data class MusicPlayerTapped(val album: AlbumWithArtistName, val musicPlayer: MusicPlayer) : AlbumListEvents
}
