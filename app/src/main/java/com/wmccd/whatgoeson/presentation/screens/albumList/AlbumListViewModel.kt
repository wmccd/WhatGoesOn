package com.wmccd.whatgoeson.presentation.screens.albumList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.repository.database.Album
import com.wmccd.whatgoeson.repository.database.AlbumWithArtistName
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AlbumListViewModel(
    mockedUiStateForTestingAndPreviews: AlbumListUiState? = null
) : ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(AlbumListUiState())
    val uiState: StateFlow<AlbumListUiState> = _uiState.asStateFlow()

    //keeps track of when we want to navigate to another screen
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

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
        try {
            //stop showing the loading screen spinner
            //start showing the screen data
            _uiState.value = uiState.value.copy(
                isLoading = false,
                data = fetchUiData()
            )
        } catch (ex: Exception) {
            //something went wrong, show the error message
            MyApplication.utilities.logger.log(Log.ERROR, TAG, "fetching Live Data: Exception", ex)
            _uiState.value = uiState.value.copy(
                error = ex.message
            )
        }
    }

    private suspend fun fetchUiData(): AlbumListUiData {
        var list: List<AlbumWithArtistName>
        runBlocking {
            list = MyApplication.repository.appDatabase.albumDao().getAllDetails().first()
        }
        MyApplication.utilities.logger.log(Log.INFO, TAG, "fetchUiData: ${list.size}")
        return AlbumListUiData(
            albumList = list
        )
    }

    fun onEvent(event: AlbumListEvents) {
        //the user tapped on something on the screen and we need to handle that
        MyApplication.utilities.logger.log(Log.INFO, TAG, "onEvent $event")
        when (event) {
            AlbumListEvents.ButtonClicked -> onActionButtonClicked()
            is AlbumListEvents.DeleteAlbum -> onDeleteAlbum(event.album)
            is AlbumListEvents.LongClicked -> onLongClicked(event.clicked, album = event.album)
        }
    }

    private fun onLongClicked(clicked: Boolean, album: AlbumWithArtistName? = null) {
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
    val albumSelectedForDelete: AlbumWithArtistName? = null
)

sealed interface AlbumListEvents {
    data object ButtonClicked : AlbumListEvents
    data class LongClicked(val clicked: Boolean, val album: AlbumWithArtistName?) : AlbumListEvents
    data class DeleteAlbum(val album: AlbumWithArtistName) : AlbumListEvents
}
