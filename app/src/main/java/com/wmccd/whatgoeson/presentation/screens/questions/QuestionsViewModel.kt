package com.wmccd.whatgoeson.presentation.screens.questions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion.SimpleQuestionPromptModel
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion.SimpleQuestionPromptType
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion.SimpleQuestionPrompter
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion.responsemodels.SimpleQuestionResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuestionsViewModel(
    mockedUiStateForTestingAndPreviews: QuestionsUiState? = null
) : ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(QuestionsUiState())
    val uiState: StateFlow<QuestionsUiState> = _uiState.asStateFlow()

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

    private fun mockedUiStateMode(uiStateForTestingAndPreviews: QuestionsUiState) {
        _uiState.value = uiStateForTestingAndPreviews
    }

    private fun liveData() {
        MyApplication.utilities.logger.log(Log.INFO, TAG, "fetching Live Data")
        //Update the state to indicate that we are fetching data
        _uiState.value = QuestionsUiState(isLoading = true)
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

    private suspend fun fetchUiData(): QuestionsUiData {
        return QuestionsUiData(
            someData = "Hello",
            questionTypes = SimpleQuestionPromptType.entries,
            artistNames = MyApplication.repository.appDatabase.artistDao().getAllArtists().first().map { it.artistName }.sorted(),
        )
    }

    fun onEvent(event: QuestionsEvents) {
        //the user tapped on something on the screen and we need to handle that
        MyApplication.utilities.logger.log(Log.INFO, TAG, "onEvent ")
        when (event) {
            QuestionsEvents.SubmitClicked -> onSubmitButtonClicked()
            is QuestionsEvents.ArtistSelected -> onArtistSelected(event.selectedArtist)
            is QuestionsEvents.QuestionSelected -> onQuestionSelected(event.selectedQuestionPromptType)
        }
    }

    private fun onQuestionSelected(selectedQuestionPromptType: SimpleQuestionPromptType) {
        _uiState.value = uiState.value.copy(
            data = uiState.value.data?.copy(
                selectedQuestionPromptType = selectedQuestionPromptType
            )
        )
    }

    private fun onArtistSelected(selectedArtist: String) {
        _uiState.value = uiState.value.copy(
            data = uiState.value.data?.copy(
                selectedArtist = selectedArtist
            )
        )
    }

    private fun onSubmitButtonClicked() {
        setProcessing(true)
        val artistList = if(uiState.value.data?.selectedQuestionPromptType == SimpleQuestionPromptType.SUPER_GROUP){
            listOf(
                uiState.value.data?.selectedArtist.orEmpty(),
                uiState.value.data?.artistNames?.random().orEmpty(),
                uiState.value.data?.artistNames?.random().orEmpty()
            )
        }else{
            listOf( uiState.value.data?.selectedArtist.orEmpty())
        }
        SimpleQuestionPrompter().prompt(
            promptModel = SimpleQuestionPromptModel(
                questionType = uiState.value.data?.selectedQuestionPromptType?: SimpleQuestionPromptType.HIGHLY_RATED_FIVE_YEARS,
                artistNames = artistList,
                success = {
                    setProcessing(false)
                    MyApplication.utilities.logger.log(Log.INFO, TAG, "In success callback ${it}")
                    _uiState.value = uiState.value.copy(
                        data = uiState.value.data?.copy(
                            simpleQuestionResponse = it
                        )
                    )
                },
                failure = {
                    setProcessing(false)
                    MyApplication.utilities.logger.log(Log.INFO, TAG, "In failure callback ${it}")
                    _uiState.value = uiState.value.copy(
                        data = uiState.value.data?.copy(
                            simpleQuestionResponse = null
                        )
                    )
                }
            )
        )
    }

    fun setProcessing(processing: Boolean){
        _uiState.value = uiState.value.copy(
            data = uiState.value.data?.copy(
                processing = processing
            )
        )
    }

    companion object {
        private val TAG = QuestionsViewModel::class.java.simpleName
    }
}

data class QuestionsUiState(
    val isLoading: Boolean = false,
    val data: QuestionsUiData? = null,
    val error: String? = null
)

data class QuestionsUiData(
    val someData: String? = "",
    val selectedQuestionPromptType: SimpleQuestionPromptType? = null,
    val questionTypes: List<SimpleQuestionPromptType>? = null,
    val selectedArtist: String? = null,
    val artistNames: List<String>? = null,
    val simpleQuestionResponse: SimpleQuestionResponse? = null,
    val processing: Boolean = false
)

sealed interface QuestionsEvents {
    data object SubmitClicked : QuestionsEvents
    data class QuestionSelected(val selectedQuestionPromptType: SimpleQuestionPromptType) : QuestionsEvents
    data class ArtistSelected(val selectedArtist: String) : QuestionsEvents
}
