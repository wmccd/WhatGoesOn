package com.wmccd.whatgoeson.presentation.screens.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.DisplayLoading
import com.wmccd.whatgoeson.presentation.screens.common.NavigationEvent
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.presentation.screens.common.STANDARD_SCREEN_PADDING
import java.util.UUID

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
                    //TODO Add Destination Route
                    //navController.navigate(NavigationEnum.AddDestination.route)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayData(
    data: SettingsUiData,
    onEvent: (SettingsEvents) -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(STANDARD_SCREEN_PADDING),
        contentAlignment = Alignment.TopStart
    ) {
        LazyColumn {
            items(data.menuItems.size) { index ->
                val menuItem = data.menuItems[index]
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
                        painter = painterResource(id =menuItem.iconId),
                        contentDescription = stringResource(menuItem.titleId),
                        modifier = Modifier.weight(0.2f)
                    )
                    Text(
                        text = stringResource(menuItem.titleId),
                        modifier = Modifier.weight(0.8f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (index < data.menuItems.lastIndex) {
                    HorizontalDivider()
                }
            }
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