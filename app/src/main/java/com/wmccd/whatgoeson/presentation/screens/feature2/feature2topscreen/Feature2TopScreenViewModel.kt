package com.wmccd.whatgoeson.presentation.screens.feature2.feature2topscreen

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

class Feature2TopScreenViewModel(
) : ViewModel() {

    private val _uiState = MutableStateFlow(Feature2TopScreenUiState())
    val uiState: StateFlow<Feature2TopScreenUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        MyApplication.utilities.logger.log(Log.INFO, TAG, "init1")
        _uiState.value = Feature2TopScreenUiState(isLoading = true)
        fetchData()
    }

    private fun fetchData() {
        //fetch the data and update the screen state
        try {
            //stop showing the loading screen spinner
            _uiState.value = Feature2TopScreenUiState(isLoading = false)

            //start displaying the screen data
            _uiState.value = Feature2TopScreenUiState(data = true)
        }catch (ex: Exception){
            //something went wrong, show the error message
            MyApplication.utilities.logger.log(Log.ERROR, TAG, "fetchData", ex)
            _uiState.value = Feature2TopScreenUiState(error = ex.message)
        }
    }

    fun onEvent(event: Feature2TopScreenEvents) {
        //the user tapped on something on the screen and we need to handle that
        when (event) {
            Feature2TopScreenEvents.ButtonClicked -> onActionButtonClicked()
        }
    }

    private fun onActionButtonClicked() {
        //the main action button was clicked and we want to move to next screen
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToNextScreen)
        }
    }

    companion object{
        private const val TAG = "Feature2TopScreenViewModel"
    }
}

data class Feature2TopScreenUiState(
    val isLoading: Boolean = false,
    val data: Any? = null, // Replace with your actual data type
    val error: String? = null
)

sealed interface Feature2TopScreenEvents{
    object ButtonClicked: Feature2TopScreenEvents
}