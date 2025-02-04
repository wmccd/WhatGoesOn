package com.wmccd.whatgoeson.presentation.screens.newAddition.newAlbumTopScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewAdditionTopScreenViewModel(
    mockedUiStateForTestingAndPreviews: NewAlbumTopScreenUiState? = null
) : ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(NewAlbumTopScreenUiState())
    val uiState: StateFlow<NewAlbumTopScreenUiState> = _uiState.asStateFlow()

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

    private fun mockedUiStateMode(uiStateForTestingAndPreviews: NewAlbumTopScreenUiState) {
        _uiState.value = uiStateForTestingAndPreviews
    }

    private fun liveData() {
        MyApplication.utilities.logger.log(Log.INFO, TAG, "fetching Live Data")
        _uiState.value = NewAlbumTopScreenUiState(isLoading = true)
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

    private suspend fun fetchUiData(): NewAlbumTopScreenUiData {
        return NewAlbumTopScreenUiData()
    }

    fun onEvent(event: NewAlbumTopScreenEvents) {
        //the user tapped on something on the screen and we need to handle that
        MyApplication.utilities.logger.log(Log.INFO, TAG, "onEvent $event")
        when (event) {
            NewAlbumTopScreenEvents.ButtonClicked -> onActionButtonClicked()
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

data class NewAlbumTopScreenUiState(
    val isLoading: Boolean = false,
    val data: NewAlbumTopScreenUiData? = null,
    val error: String? = null
)

data class NewAlbumTopScreenUiData(
    val albumName: String? = "",
    val artistName: String? = "",
    val imageUrl: String? = "",
    val someData: String? = "",
    val ctaEnabled: Boolean = false
)

sealed interface NewAlbumTopScreenEvents{
    data object ButtonClicked: NewAlbumTopScreenEvents
}