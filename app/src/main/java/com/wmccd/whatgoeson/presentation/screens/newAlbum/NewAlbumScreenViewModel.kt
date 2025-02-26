package com.wmccd.whatgoeson.presentation.screens.newAlbum

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.composables.INTERNET_IMAGE_NOT_AVAILABLE
import com.wmccd.whatgoeson.repository.database.Album
import com.wmccd.whatgoeson.repository.database.Artist
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class NewAdditionScreenViewModel(
    mockedUiStateForTestingAndPreviews: NewAlbumScreenUiState? = null
) : ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(NewAlbumScreenUiState())
    val uiState: StateFlow<NewAlbumScreenUiState> = _uiState.asStateFlow()

    //keeps track of when we want to navigate to another screen
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        //The init block **only** runs when the ViewModel is created
        if(mockedUiStateForTestingAndPreviews == null)
            liveData()
        else
            mockedUiStateMode(mockedUiStateForTestingAndPreviews)
    }

    private fun mockedUiStateMode(uiStateForTestingAndPreviews: NewAlbumScreenUiState) {
        _uiState.value = uiStateForTestingAndPreviews
    }

    private fun liveData() {
        MyApplication.utilities.logger.log(Log.INFO, TAG, "fetching Live Data")
        _uiState.value = NewAlbumScreenUiState(isLoading = true)
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
        }catch (ex: Exception){
            //something went wrong, show the error message
            MyApplication.utilities.logger.log(Log.ERROR, TAG, "fetching Live Data: Exception", ex)
            _uiState.value = uiState.value.copy(
                error = ex.message
            )
        }
    }

    private suspend fun fetchUiData(): NewAlbumScreenUiData {
        return NewAlbumScreenUiData(
            albumName = "",
            artistName = "",
            imageUrl = "",
            saveButtonEnabled = false,
            artistIdNameList = fetchArtistIdNameList(),
            displayAddAlbumDemoText = MyApplication.repository.appDatabase.albumDao().getAlbumCount().first() <= 5
        )
    }

    private suspend fun fetchArtistIdNameList(): List<Pair<Long, String>> {
        val allArtists = MyApplication.repository.appDatabase.artistDao().getAllArtists().first()
        return allArtists.map { it.id to it.artistName }.sortedBy { it.second }
    }

    fun onEvent(event: NewAlbumScreenEvents) {
        //the user tapped on something on the screen and we need to handle that
        MyApplication.utilities.logger.log(Log.INFO, TAG, "onEvent $event")
        when (event) {
            NewAlbumScreenEvents.SaveButtonClicked -> onSaveButtonClicked()
            is NewAlbumScreenEvents.AlbumImageUrlChanged -> onAlbumImageUrlChanged(event.imageUrl)
            is NewAlbumScreenEvents.AlbumNameChanged -> onAlbumNameChanged(event.albumName)
            is NewAlbumScreenEvents.ArtistNameChanged -> onArtistNameChanged(event.artistName)
            is NewAlbumScreenEvents.ArtistSelected -> onArtistSelected(event.artistId, event.artistName)
            NewAlbumScreenEvents.NoImageUrlAvailableClicked -> onNoImageUrlAvailableClicked()
            NewAlbumScreenEvents.CloseVideoPlayer -> toggleVideoPlayerDisplay()
            NewAlbumScreenEvents.OpenVideoPlayer -> toggleVideoPlayerDisplay()
        }
    }

    private fun toggleVideoPlayerDisplay() {
        val currentValue = uiState.value.data?.displayVideoPlayer ?: false
        _uiState.value = _uiState.value.copy(
            data = _uiState.value.data?.copy(
                displayVideoPlayer = !currentValue
            )
        )
    }

    private fun onNoImageUrlAvailableClicked() {
        _uiState.value = _uiState.value.copy(
            data = _uiState.value.data?.copy(
                imageUrl = INTERNET_IMAGE_NOT_AVAILABLE
            )
        )
        checkIfSaveButtonShouldBeEnabled()
    }

    private fun onAlbumImageUrlChanged(imageUrl: String) {
        _uiState.value = _uiState.value.copy(
            data = _uiState.value.data?.copy(
                imageUrl = imageUrl
            )
        )
        checkIfSaveButtonShouldBeEnabled()
    }

    private fun onAlbumNameChanged(albumName: String) {
        _uiState.value = _uiState.value.copy(
            data = _uiState.value.data?.copy(
                albumName = albumName
            )
        )
        checkIfSaveButtonShouldBeEnabled()
    }

    private fun onArtistNameChanged(artistName: String) {
        _uiState.value = _uiState.value.copy(
            data = _uiState.value.data?.copy(
                artistName = artistName
            )
        )
        checkIfSaveButtonShouldBeEnabled()
    }

    private fun onArtistSelected(artistId: Long, artistName: String) {
        _uiState.value = _uiState.value.copy(
            data = _uiState.value.data?.copy(
                artistName = artistName,
                artistId = artistId
            )
        )
        checkIfSaveButtonShouldBeEnabled()
    }

    private fun onSaveButtonClicked() {
        //saving the details to the database
        viewModelScope.launch {

            try {
                // Check if this artist is already in the database
                val artist = MyApplication.repository.appDatabase.artistDao()
                    .getArtistByString(
                        uiState.value.data?.artistName.orEmpty()
                    ).firstOrNull()

                if (artist == null) {
                    insertArtistAndAlbum()
                } else {
                    insertAlbum(artistId = artist.id)
                }
                _uiState.value = _uiState.value.copy(
                    data = fetchUiData()
                )
            }catch(ex: Exception){
                MyApplication.utilities.logger.log(Log.ERROR, TAG, "onSaveButtonClicked: Exception", ex)
                _uiState.value = uiState.value.copy(
                    error = ex.message
                )
            }
        }
    }

    private suspend fun insertArtistAndAlbum() {
        val artistId = MyApplication.repository.appDatabase.artistDao().insert(
            Artist(
                artistName = uiState.value.data?.artistName.orEmpty()
            )
        )
        insertAlbum(artistId = artistId)
    }

    private suspend fun insertAlbum(artistId: Long) {

        val allAlbums = MyApplication.repository.appDatabase.albumDao().getAllAlbums().first()
        val existingMatch = allAlbums.firstOrNull {
            it.artistId == artistId && it.name == uiState.value.data?.albumName
        }

        if(existingMatch == null){
            MyApplication.repository.appDatabase.albumDao().insert(
                Album(
                    artistId = artistId,
                    name = uiState.value.data?.albumName.orEmpty(),
                    imageUrl = uiState.value.data?.imageUrl.orEmpty()
                )
            )
            Toast.makeText(MyApplication.appContext, "Album saved", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(MyApplication.appContext, "Album already exists", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkIfSaveButtonShouldBeEnabled() {
        val allFieldsFilled = _uiState.value.data?.artistName?.isNotBlank() ==true &&
            _uiState.value.data?.albumName?.isNotBlank() ==true &&
            _uiState.value.data?.imageUrl?.isNotBlank() ==true

        _uiState.value = _uiState.value.copy(
            data = _uiState.value.data?.copy(
                saveButtonEnabled = allFieldsFilled
            )
        )
    }

    companion object{
        private val TAG = this::class.java.simpleName
    }
}

data class NewAlbumScreenUiState(
    val isLoading: Boolean = false,
    val data: NewAlbumScreenUiData? = null,
    val error: String? = null
)

data class NewAlbumScreenUiData(
    val albumName: String? = "",
    val artistId: Long = -1,
    val artistName: String? = "",
    val imageUrl: String? = "",
    val artistIdNameList: List<Pair<Long, String>> = emptyList(),
    val saveButtonEnabled: Boolean = false,
    val displayAddAlbumDemoText: Boolean = false,
    val displayVideoPlayer: Boolean = false
)

sealed interface NewAlbumScreenEvents{
    data class ArtistSelected(val artistId: Long, val artistName: String): NewAlbumScreenEvents
    data class ArtistNameChanged(val artistName: String): NewAlbumScreenEvents
    data class AlbumNameChanged(val albumName: String): NewAlbumScreenEvents
    data class AlbumImageUrlChanged(val imageUrl: String): NewAlbumScreenEvents
    data object NoImageUrlAvailableClicked: NewAlbumScreenEvents
    data object SaveButtonClicked: NewAlbumScreenEvents
    data object OpenVideoPlayer: NewAlbumScreenEvents
    data object CloseVideoPlayer: NewAlbumScreenEvents
}