package com.wmccd.whatgoeson.presentation.screens.settings

import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.utility.csvImportExport.CsvExporter
import com.wmccd.whatgoeson.utility.csvImportExport.CsvImporter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    mockedUiStateForTestingAndPreviews: SettingsUiState? = null
) : ViewModel() {

    //Keeps track of the current data that is to be displayed on the screen
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

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

    private fun mockedUiStateMode(uiStateForTestingAndPreviews: SettingsUiState) {
        _uiState.value = uiStateForTestingAndPreviews
    }

    private fun liveData() {
        MyApplication.utilities.logger.log(Log.INFO, TAG, "fetching Live Data")
        //Update the state to indicate that we are fetching data
        _uiState.value = SettingsUiState(isLoading = true)
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

    private suspend fun fetchUiData(): SettingsUiData {
        return SettingsUiData(
            menuItems = listOf(
                MenuItem(
                    titleId = R.string.import_text,
                    iconId = R.drawable.ic_file_import,
                ),
                MenuItem(
                    titleId = R.string.export,
                    iconId = R.drawable.ic_file_export,
                ),
                MenuItem(
                    titleId = R.string.add_album_demo,
                    iconId = R.drawable.ic_video,
                ),
            )
        )
    }

    fun onEvent(event: SettingsEvents) {
        //the user tapped on something on the screen and we need to handle that
        MyApplication.utilities.logger.log(Log.INFO, TAG, "onEvent ")
        when (event) {
            is SettingsEvents.MenuItemClicked -> onMenuItemClicked(event.menuItem)
            is SettingsEvents.ImportFileSelected -> onImportFileSelected(event.uri)
            SettingsEvents.CloseVideoPlayer -> onVideoPlayerDisplayToggle()
        }
    }

    private fun onImportFileSelected(uri: Uri) {
        viewModelScope.launch {
            MyApplication.appContext.contentResolver.openInputStream(uri)?.use { inputStream ->
                CsvImporter(
                    MyApplication.repository.appDatabase.albumDao(),
                    MyApplication.repository.appDatabase.artistDao()
                ).importData(inputStream)
            }
        }
        _uiState.value = uiState.value.copy(
            data = uiState.value.data?.copy(
                displayImportFilePicker = false
            )
        )
    }

    private fun onMenuItemClicked(menuItem: MenuItem) {
        //the main action button was clicked and we want to move to next screen
        viewModelScope.launch {
            when (menuItem.titleId) {
                R.string.export -> onExportClicked()
                R.string.import_text -> onImportClicked()
                R.string.add_album_demo -> onVideoPlayerDisplayToggle()
            }
        }
    }

    private fun onVideoPlayerDisplayToggle() {
        val currentValue = uiState.value.data?.displayVideoPlayer ?: false
        _uiState.value = uiState.value.copy(
            data = uiState.value.data?.copy(
                displayVideoPlayer = !currentValue
            )
        )
    }

    private fun onImportClicked() {
        val currentValue = uiState.value.data?.displayImportFilePicker ?: false
        _uiState.value = uiState.value.copy(
            data = uiState.value.data?.copy(
                displayImportFilePicker = !currentValue
            )
        )
    }

    private suspend fun onExportClicked() {
        try {
            CsvExporter(
                MyApplication.repository.appDatabase.albumDao()
            ).export()
        } catch (e: Exception) {
            MyApplication.utilities.logger.log(Log.ERROR, TAG, "onExportClicked: Exception", e)
        }
    }

    companion object {
        private val TAG = SettingsViewModel::class.java.simpleName
    }
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val data: SettingsUiData? = null,
    val error: String? = null
)

data class SettingsUiData(
    val menuItems: List<MenuItem> = emptyList(),
    val displayImportFilePicker: Boolean = false,
    val displayVideoPlayer: Boolean = false
)

data class MenuItem(
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int,
)

sealed interface SettingsEvents {
    data class MenuItemClicked(val menuItem: MenuItem) : SettingsEvents
    data class ImportFileSelected(val uri: Uri) : SettingsEvents
    data object CloseVideoPlayer : SettingsEvents
}
