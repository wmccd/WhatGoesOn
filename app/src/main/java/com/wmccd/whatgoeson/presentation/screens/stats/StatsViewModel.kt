package com.wmccd.whatgoeson.presentation.screens.stats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.repository.database.AlbumArtistCount
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StatsViewModel(
    mockedUiStateForTestingAndPreviews: StatsUiState? = null
) : ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

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

    private fun mockedUiStateMode(uiStateForTestingAndPreviews: StatsUiState) {
        _uiState.value = uiStateForTestingAndPreviews
    }

    private fun liveData() {
        MyApplication.utilities.logger.log(Log.INFO, TAG, "fetching Live Data")
        //Update the state to indicate that we are fetching data
        _uiState.value = StatsUiState(isLoading = true)
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

    private suspend fun fetchUiData(): StatsUiData {
        val allAlbums = MyApplication.repository.appDatabase.albumDao().getAllAlbums()
        val allArtists = MyApplication.repository.appDatabase.artistDao().getAllArtists()
        val artistAlbumCount = MyApplication.repository.appDatabase.albumDao().getAlbumArtistCountList()
        return StatsUiData(
            albumCount = allAlbums.first().size,
            artistCount = allArtists.first().size,
            artistAlbumCount = artistAlbumCount.first()
        )
    }

    fun onEvent(event: StatsEvents) {
        //the user tapped on something on the screen and we need to handle that
        MyApplication.utilities.logger.log(Log.INFO, TAG, "onEvent ")
        when (event) {
            StatsEvents.ButtonClicked -> onActionButtonClicked()
        }
    }

    private fun onActionButtonClicked() {
        //the main action button was clicked and we want to move to next screen
        viewModelScope.launch {
            //_navigationEvent.emit(NavigationEvent.NavigateToNextScreen)
        }
    }

    companion object {
        private val TAG = StatsViewModel::class.java.simpleName
    }
}

data class StatsUiState(
    val isLoading: Boolean = false,
    val data: StatsUiData? = null,
    val error: String? = null
)

data class StatsUiData(
    val someData: String? = "",
    val albumCount: Int = 0,
    val artistCount: Int = 0,
    val artistAlbumCount: List<AlbumArtistCount> = emptyList(),
)

sealed interface StatsEvents {
    data object ButtonClicked : StatsEvents
}
