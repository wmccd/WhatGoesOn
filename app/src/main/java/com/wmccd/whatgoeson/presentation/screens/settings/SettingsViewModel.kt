package com.wmccd.whatgoeson.presentation.screens.settings

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

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
                    titleId = R.string.export,
                    iconId = R.drawable.ic_file_export,
                ),
            )
        )
    }

    fun onEvent(event: SettingsEvents) {
        //the user tapped on something on the screen and we need to handle that
        MyApplication.utilities.logger.log(Log.INFO, TAG, "onEvent ")
        when (event) {
            is SettingsEvents.MenuItemClicked -> onMenuItemClicked(event.menuItem)
        }
    }

    private fun onMenuItemClicked(menuItem: MenuItem) {
        //the main action button was clicked and we want to move to next screen
        viewModelScope.launch {
            when (menuItem.titleId) {
                R.string.export -> onExportClicked()
            }
        }
    }

    private suspend fun onExportClicked() {
        try {
            // 1. Read Data from Room
            val allDetails =
                MyApplication.repository.appDatabase.albumDao().getAllDetails().firstOrNull()
                    ?: emptyList()

            // 2. Format Data (CSV)
            val csvContent = buildString {
                // CSV Header
                appendLine("Album ID,Album Name,Album URL,Album Favourite,Artist ID,Artist Name")
                allDetails.forEach {
                    val albumName = it.albumName.replace(",", "|")
                    val artistName = it.artistName.replace(",", "|")
                    appendLine("${it.albumId},$albumName,${it.albumUrl},${it.albumFavourite},${it.artistId},$artistName")
                }
            }

            // 3. Create a File
            val directory =
                MyApplication.appContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(directory, "all_details_export.csv")

            // 4. Write Data to File
            FileWriter(file).use { writer ->
                writer.write(csvContent)
            }

            // 5. Create an Email Intent
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                val fileUri: Uri = FileProvider.getUriForFile(
                    MyApplication.appContext,
                    "${MyApplication.appContext.packageName}.fileprovider",
                    file
                )
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "What Goes On - Data Export")
                putExtra(Intent.EXTRA_TEXT, "Here's the exported all details data. Note that commas have been replaced with pipes.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // 6. Start the Email Activity
            val chooserIntent = Intent.createChooser(emailIntent, "Send email...")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.appContext.startActivity(chooserIntent)

            // 7. Delete the temporary file
            file.deleteOnExit()

        } catch (e: Exception) {
            // Handle error
            MyApplication.utilities.logger.log(
                Log.ERROR,
                TAG,
                "onExportClicked: Exception",
                e
            )
        }    }

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
    val menuItems: List<MenuItem> = emptyList()
)

data class MenuItem(
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int,
)

sealed interface SettingsEvents {
    data class MenuItemClicked(val menuItem: MenuItem) : SettingsEvents
}
