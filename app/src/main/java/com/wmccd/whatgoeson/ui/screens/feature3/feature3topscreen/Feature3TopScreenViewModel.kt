package com.wmccd.whatgoeson.ui.screens.feature3.feature3topscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.ui.screens.common.NavigationEvent

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TopScreen3ViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(Feature3TopScreenUiState())
    val uiState: StateFlow<Feature3TopScreenUiState> = _uiState.asStateFlow()

    //keeps track of when we want to navigate to another screen
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()


    init {
        _uiState.value = Feature3TopScreenUiState(isLoading = true)
        fetchData()
    }

    private fun fetchData() {
        //fetch the data and update the screen state
        try {
            //stop showing the loading screen spinner
            _uiState.value = Feature3TopScreenUiState(isLoading = false)

            //start displaying the screen data
            _uiState.value = Feature3TopScreenUiState(data = true)
        }catch (ex: Exception){
            //something went wrong, show the error message
            MyApplication.utilities.logger.log(Log.ERROR, TAG, "fetchData", ex)
            _uiState.value = Feature3TopScreenUiState(error = ex.message)
        }
    }

    fun onEvent(event: Feature3TopScreenEvents) {
        //the user tapped on something on the screen and we need to handle that
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
        private const val TAG = "Feature3TopScreenViewModel"
    }
}

data class Feature3TopScreenUiState(
    val isLoading: Boolean = false,
    val data: Any? = null, // Replace with your actual data type
    val error: String? = null
)

sealed interface Feature3TopScreenEvents{
    data object ButtonClicked: Feature3TopScreenEvents
}