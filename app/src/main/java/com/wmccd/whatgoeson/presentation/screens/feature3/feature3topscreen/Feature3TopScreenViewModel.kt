package com.wmccd.whatgoeson.presentation.screens.feature3.feature3topscreen

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

class TopScreen3ViewModel(
    mockedUiStateForTestingAndPreviews: Feature3TopScreenUiState? = null
) : ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(Feature3TopScreenUiState())
    val uiState: StateFlow<Feature3TopScreenUiState> = _uiState.asStateFlow()

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

    private fun mockedUiStateMode(uiStateForTestingAndPreviews: Feature3TopScreenUiState) {
        _uiState.value = uiStateForTestingAndPreviews
    }

    private fun liveData() {
        MyApplication.utilities.logger.log(Log.INFO, TAG, "fetching live data")
        _uiState.value = Feature3TopScreenUiState(isLoading = true)
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
            MyApplication.utilities.logger.log(Log.ERROR, TAG, "fetching live data: Exception", ex)
            _uiState.value = uiState.value.copy(
                error = ex.message
            )
        }
    }

    private suspend fun fetchUiData(): Feature3TopScreenUiData {
        return Feature3TopScreenUiData()
    }

    fun onEvent(event: Feature3TopScreenEvents) {
        //the user tapped on something on the screen and we need to handle that
        MyApplication.utilities.logger.log(Log.INFO, TAG, "onEvent $event")
        when (event) {
            Feature3TopScreenEvents.ButtonClicked -> onActionButtonClicked()
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

data class Feature3TopScreenUiState(
    val isLoading: Boolean = false,
    val data: Feature3TopScreenUiData? = null,
    val error: String? = null
)

data class Feature3TopScreenUiData(
    val someData: String? = "",
)

sealed interface Feature3TopScreenEvents{
    data object ButtonClicked: Feature3TopScreenEvents
}