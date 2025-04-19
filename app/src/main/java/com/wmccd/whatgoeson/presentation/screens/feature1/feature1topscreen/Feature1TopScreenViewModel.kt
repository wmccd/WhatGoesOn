package com.wmccd.whatgoeson.presentation.screens.feature1.feature1topscreen

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class Feature1TopScreenViewModel(
    mockedUiStateForTestingAndPreviews: Feature1TopScreenUiState? = null
): ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(Feature1TopScreenUiState())
    val uiState: StateFlow<Feature1TopScreenUiState> = _uiState.asStateFlow()

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

    private fun mockedUiStateMode(uiStateForTestingAndPreviews: Feature1TopScreenUiState) {
        _uiState.value = uiStateForTestingAndPreviews
    }

    private fun liveData() {
        MyApplication.utilities.logger.log(Log.INFO, TAG, "fetching live data")
        _uiState.value = Feature1TopScreenUiState(isLoading = true)
        viewModelScope.launch {
            MyApplication.repository.appDataStore.updateUserName("Bobbins ${System.currentTimeMillis().toString().takeLast(4)}")
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

    private suspend fun fetchUiData(): Feature1TopScreenUiData{
        val response = MyApplication.repository.setListFmApiService.searchArtists("The Beatles")
        return Feature1TopScreenUiData(
            randomText = MyApplication.repository.appDataStore.userNameFlow.first().orEmpty(),
            randomLong = System.currentTimeMillis(),
            randomInt = 0
        )
    }


    fun onEvent(event: Feature1TopScreenEvents) {
        //the user tapped on something on the screen and we need to handle that
        MyApplication.utilities.logger.log(Log.INFO, TAG, "onEvent $event")
        when (event) {
            Feature1TopScreenEvents.ButtonClicked -> onActionButtonClicked()
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

data class Feature1TopScreenUiState(
    val isLoading: Boolean = false,
    val data: Feature1TopScreenUiData? = null, // Replace with your actual data type
    val error: String? = null
)

data class Feature1TopScreenUiData(
    val randomText: String = "",
    val randomLong: Long = 0L,
    val randomInt: Int = 0
)

sealed interface Feature1TopScreenEvents{
    data object ButtonClicked: Feature1TopScreenEvents
}