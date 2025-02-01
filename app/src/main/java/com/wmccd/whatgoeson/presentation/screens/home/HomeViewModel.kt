package com.wmccd.whatgoeson.presentation.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.repository.database.Album
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    mockedUiStateForTestingAndPreviews: HomeUiState? = null
) : ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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

    private fun mockedUiStateMode(uiStateForTestingAndPreviews: HomeUiState) {
        _uiState.value = uiStateForTestingAndPreviews
    }

    private fun liveData() {
        MyApplication.utilities.logger.log(Log.INFO, TAG, "fetching live data")
        _uiState.value = HomeUiState(isLoading = true)
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
            MyApplication.utilities.logger.log(Log.ERROR, TAG, "fetching live data: Exception", ex)
            _uiState.value = uiState.value.copy(
                error = ex.message
            )
        }
    }

    private suspend fun fetchUiData(): HomeUiData {
        val allAlbums = MyApplication.repository.appDatabase.albumDao().getAllAlbums().first()
        val uiData = if(allAlbums.isEmpty()) {
            noAlbumsStored()
        }else{
            randomAlbum( allAlbums = allAlbums )
        }
        
        return uiData
    }

    private fun noAlbumsStored() = HomeUiData(
        noAlbumsStored = true,
    )

    private suspend fun randomAlbum(allAlbums: List<Album>): HomeUiData {
        val randomAlbum = allAlbums.random()
        val randomArtist = MyApplication.repository.appDatabase.artistDao().getArtistById(randomAlbum.artistId).first()
        val uiData = HomeUiData(
            artistName = randomArtist.artistName,
            albumName = randomAlbum.name,
            albumArtUrl = randomAlbum.imageUrl,
            noAlbumsStored = false,
        )
        return uiData
    }

    fun onEvent(event: HomeEvents) {
        //the user tapped on something on the screen and we need to handle that
        MyApplication.utilities.logger.log(Log.INFO, TAG, "onEvent $event")
        when (event) {
            HomeEvents.ButtonClicked -> onActionButtonClicked()
        }
    }

    private fun onActionButtonClicked() {
        //the main action button was clicked and we want to move to next screen
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToNextScreen)
        }
    }

    companion object{
        private val TAG = this::class.java.simpleName
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val data: HomeUiData? = null,
    val error: String? = null
)

data class HomeUiData(
    val artistName: String? = null,
    val albumName: String? = null,
    val albumArtUrl: String? = null,
    val noAlbumsStored: Boolean,
)

sealed interface HomeEvents {
    data object ButtonClicked : HomeEvents
}