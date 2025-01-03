package com.wmccd.whatgoeson.presentation.screens.feature1.feature1subscreen1

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class Feature1SubScreen1ViewModel: ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(Feature1SubScreen1UiState())
    val uiState: StateFlow<Feature1SubScreen1UiState> = _uiState.asStateFlow()

    //keeps track of when we want to navigate to another screen
    private val _messageChannel = Channel<NavigationEvent>()
    val messageFlow = _messageChannel.receiveAsFlow()

    init {
         _uiState.value = Feature1SubScreen1UiState(isLoading = true)
        fetchData()
    }

    private fun fetchData() {
        //fetch the data and update the screen state
        try {
            //stop showing the loading screen spinner
            _uiState.value = Feature1SubScreen1UiState(isLoading = false)

            //start displaying the screen data
            _uiState.value = Feature1SubScreen1UiState(
                data = Feature1SubScreen1UiState()
            )
        }catch (ex: Exception){
            //something went wrong, show the error message
            MyApplication.utilities.logger.log(Log.ERROR, TAG, "fetchData", ex)
            _uiState.value = Feature1SubScreen1UiState(error = ex.message)
        }
    }

    fun onEvent(event: Feature1SubScreen1Events) {
        //the user tapped on something on the screen and we need to handle that
        when (event) {
            Feature1SubScreen1Events.ButtonClicked -> onActionButtonClicked()
        }
    }

    private fun onActionButtonClicked() {
        //the main action button was clicked and we want to move to next screen
        viewModelScope.launch {
            viewModelScope.launch {
                MyApplication.utilities.logger.log(Log.DEBUG, TAG, "onActionButtonClicked")
                _messageChannel.send(NavigationEvent.NavigateToNextScreen)
            }
        }
    }

    companion object{
        private const val TAG = "Feature1SubScreen1ViewModel"
    }
}

data class Feature1SubScreen1UiState(
    val isLoading: Boolean = false,
    val data: Any? = null, // Replace with your actual data type
    val error: String? = null
)

sealed interface Feature1SubScreen1Events{
    data object ButtonClicked: Feature1SubScreen1Events
}