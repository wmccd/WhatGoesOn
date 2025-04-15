package com.wmccd.whatgoeson.presentation.screens.albumList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.repository.database.Album
import com.wmccd.whatgoeson.repository.database.AlbumWithArtistName
import com.wmccd.whatgoeson.utility.musicPlayer.MusicPlayer
import com.wmccd.whatgoeson.utility.musicPlayer.MusicPlayerFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlbumListViewModel(
    mockedUiStateForTestingAndPreviews: AlbumListUiState? = null,
) : ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(AlbumListUiState())
    val uiState: StateFlow<AlbumListUiState> = _uiState.asStateFlow()

    //keeps track of when we want to navigate to another screen
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var spotifyInstalled: Boolean = false
    private var youTubeMusicInstalled:Boolean = false
    init {
        spotifyInstalled = MyApplication.device.spotifyInstalled
        youTubeMusicInstalled = MyApplication.device.youTubeMusicInstalled

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
            MyApplication.utilities.logger.log(Log.INFO, TAG, "fetchUiData collecting $allDetails")
            _uiState.value = uiState.value.copy(
                isLoading = false,
                data = AlbumListUiData(
                    albumList = allDetails.sortedBy { it.albumName },
                    displayDeleteDialog = _uiState.value.data?.displayDeleteDialog ?: false,
                    albumSelectedForDelete = _uiState.value.data?.albumSelectedForDelete,
                    albumSort = _uiState.value.data?.albumSort?: AlbumSort.AZ_ALBUMS,
                    filterChar = _uiState.value.data?.filterChar,
                    spotifyInstalled = spotifyInstalled,
                    youTubeMusicInstalled = youTubeMusicInstalled
                )
            )
            onSortOrderClicked(
                albumSort = _uiState.value.data?.albumSort?: AlbumSort.AZ_ALBUMS
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
            is AlbumListEvents.MusicPlayerTapped -> onMusicPlayerTapped(event.albumName, event.artistName, event.musicPlayer)
        }
    }

    private fun onMusicPlayerTapped(
        albumName: String,
        artistName: String,
        musicPlayer: MusicPlayer
    ) {
        val musicPlayerLauncher = MusicPlayerFactory(musicPlayer).create()
        musicPlayerLauncher.launch(
            artistName = artistName,
            albumName = albumName
        )
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
                    mediaType = album.mediaType,
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
                    mediaType = album.mediaType,
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



sealed interface AlbumListEvents {
    data object ButtonClicked : AlbumListEvents
    data class AlbumLongClicked(val clicked: Boolean, val album: AlbumWithArtistName?) : AlbumListEvents
    data class DeleteAlbum(val album: AlbumWithArtistName) : AlbumListEvents
    data class MarkAsFavourite(val isFavourite: Boolean, val album: AlbumWithArtistName) : AlbumListEvents
    data class SortOrderClicked(val albumSort: AlbumSort) : AlbumListEvents
    data class MusicPlayerTapped(val albumName: String, val artistName: String, val musicPlayer: MusicPlayer) : AlbumListEvents
}
