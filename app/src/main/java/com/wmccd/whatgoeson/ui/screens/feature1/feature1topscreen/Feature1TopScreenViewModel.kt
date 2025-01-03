package com.wmccd.whatgoeson.ui.screens.feature1.feature1topscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.ui.screens.common.NavigationEvent
import com.wmccd.whatgoeson.usecases.albums.InsertAlbumUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class Feature1TopScreenViewModel: ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(Feature1TopScreenUiState())
    val uiState: StateFlow<Feature1TopScreenUiState> = _uiState.asStateFlow()

    //keeps track of when we want to navigate to another screen
    private val _messageChannel = Channel<NavigationEvent>()
    val messageFlow = _messageChannel.receiveAsFlow()

    init {
        //This should only happen
        MyApplication.utilities.logger.log(Log.INFO, "TopScreen1ViewModel", "init1")

        //Update the state to indicate that we are loading data
        _uiState.value = Feature1TopScreenUiState(isLoading = true)

        viewModelScope.launch {
            MyApplication.repository.appDataStore.updateUserName("Bobbins ${System.currentTimeMillis().toString().takeLast(4)}")
            fetchData()
            InsertAlbumUseCase(MyApplication.repository.appDatabase.albumDao).execute()
        }
    }

    private suspend fun fetchData() {
        //fetch the data and update the screen state
        try {
            //start displaying the screen data
            _uiState.value = uiState.value.copy(
                isLoading = false,
                data = fetchUiData()
            )
        }catch (ex: Exception){
            //something went wrong, show the error message
            MyApplication.utilities.logger.log(Log.ERROR, TAG, "fetchData", ex)
            _uiState.value = Feature1TopScreenUiState(error = ex.message)
        }
    }

    private suspend fun fetchUiData(): Feature1TopScreenUiData{
        val response = MyApplication.repository.setListFmApi.searchArtists("The Beatles")
        val uiData = Feature1TopScreenUiData(
            randomText = MyApplication.repository.appDataStore.userNameFlow.first().orEmpty(),
            randomLong = System.currentTimeMillis(),
            randomInt = if(response.isSuccessful){
                        response.body()?.total ?:0
                    } else {-1}
        )
        return uiData
    }


    fun onEvent(event: Feature1TopScreenEvents) {
        //the user tapped on something on the screen and we need to handle that
        when (event) {
            Feature1TopScreenEvents.ButtonClicked -> onActionButtonClicked()
        }
    }

    private fun onActionButtonClicked() {
        //the main action button was clicked and we want to move to next screen
        viewModelScope.launch {
            MyApplication.utilities.logger.log(Log.DEBUG, TAG, "onActionButtonClicked")
            _messageChannel.send(NavigationEvent.NavigateToNextScreen)
        }

    }

    companion object{
        private const val TAG = "Feature1TopScreenViewModel"
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