package com.wmccd.whatgoeson.presentation.screens.settings

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.presentation.screens.common.STANDARD_SCREEN_PADDING
import com.wmccd.whatgoeson.presentation.screens.common.composables.SimpleVideoPlayerScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

private val TAG = "ImportScreen"

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = SettingsViewModel()
) {

    // Listen for navigation events sent by the ViewModel
    LaunchedEffect(key1 = UUID.randomUUID().toString()) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToNextScreen -> {
                    //navController.navigate(NavigationEnum.ImportCsvScreen.route)
                }
            }
        }
    }
    DisplayContentMode(viewModel)
}

@Composable
private fun DisplayContentMode(viewModel: SettingsViewModel) {
    // Display content based on uiState
    val uiState by viewModel.uiState.collectAsState()
    when {
        uiState.isLoading -> DisplayLoading()
        uiState.error != null -> DisplayError(uiState.error)
        uiState.data != null -> DisplayContent(
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun DisplayContent(
    uiState: SettingsUiState,
    onEvent: (SettingsEvents) -> Unit = {},
) {
    if (uiState.data == null) {
        DisplayError(stringResource(R.string.no_data_to_display))
    } else {
        DisplayData(uiState.data, onEvent)
    }
}

@Composable
fun DisplayData(
    data: SettingsUiData,
    onEvent: (SettingsEvents) -> Unit = {},
) {
    if (data.displayVideoPlayer) {
        SimpleVideoPlayerScreen(
            videoResId = R.raw.add_album_demostration,
            onEvent = {
                onEvent(SettingsEvents.CloseVideoPlayer)
            }
        )
    }else{
        DisplayMenuItems(
            data = data,
            onEvent = onEvent
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun DisplayMenuItems(
    data: SettingsUiData,
    onEvent: (SettingsEvents) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(STANDARD_SCREEN_PADDING),
        contentAlignment = Alignment.TopStart
    ) {
        LazyColumn {
            items(data.menuItems.size) { index ->
                val menuItem = data.menuItems[index]
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .combinedClickable(
                                onClick = {
                                    onEvent(SettingsEvents.MenuItemClicked(menuItem))
                                },
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = menuItem.iconId),
                            contentDescription = stringResource(menuItem.titleId),
                            modifier = Modifier
                                .weight(0.2f)
                                .height(42.dp)
                                .width(42.dp)
                        )
                        Text(
                            text = stringResource(menuItem.titleId),
                            modifier = Modifier.weight(0.8f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    AdditionalMenuItemContent(data, menuItem, onEvent)
                }
                if (index < data.menuItems.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.AdditionalMenuItemContent(
    data: SettingsUiData,
    menuItem: MenuItem,
    onEvent: (SettingsEvents) -> Unit
) {
    AnimatedVisibility(
        data.displayImportFilePicker &&
                menuItem.titleId == R.string.import_text
    ) {
        DisplayImportFilePicker(onEvent)
    }
}

@Composable
fun DisplayImportFilePicker(
    onEvent: (SettingsEvents
) -> Unit) {
    var fileUri by remember { mutableStateOf<Uri?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            fileUri = uri
        }
    )

    LaunchedEffect(key1 = fileUri) {
        fileUri?.let {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    MyApplication.utilities.logger.log(Log.ERROR, TAG, "importDataFromUri")
                    onEvent(
                        SettingsEvents.ImportFileSelected(it)
                    )
                } catch (e: Exception) {
                    MyApplication.utilities.logger.log(Log.ERROR, TAG, "importDataFromUri: Exception", e)
                }
            }
        }
    }

    //"Album ID,Album Name,Album URL,Album Favourite,Artist ID,Artist Name"
    Column {

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text ="The import format is the same as the export format so if you " +
            "are importing a file that was previously exported it will work smoothly."
        )

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = "Importing will clear the existing data and replace it with the new data."
        )

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = "The import format is CSV with the following fields:\n" +
            "• Album ID (number)\n" +
            "• Album Name (text)\n" +
            "• Album URL (text)\n" +
            "• Album Favourite (TRUE/FALSE)\n" +
            "• Artist ID (number)\n" +
            "• Artist Name (text)"
        )

        Text(modifier = Modifier.padding(vertical = 16.dp),
            text = "Note: the use of the CSV file format requires that any "+
            "commas that exist the Album Name and Artist Name fields "+
            "should be replace with a pipe character '|'")

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp),
            onClick = {
            filePickerLauncher.launch("*/*")
        }) {
            Text("Select a CSV file to import")
        }
    }
}

@Preview
@Composable
private fun PreviewDisplayData() {
    PreviewTheme {
        DisplayContent(
            uiState = SettingsUiState(
                data = SettingsUiData(
                    menuItems = listOf(
                        MenuItem(
                            titleId = R.string.export,
                            iconId = R.drawable.ic_file_export
                        ),
                        MenuItem(
                            titleId = R.string.stats,
                            iconId = R.drawable.ic_stats
                        )
                    )
                ),
            )
        )
    }
}