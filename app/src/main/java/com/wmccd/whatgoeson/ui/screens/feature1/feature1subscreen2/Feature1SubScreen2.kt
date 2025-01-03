package com.wmccd.whatgoeson.ui.screens.feature1.feature1subscreen2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.ui.screens.common.DisplayError
import com.wmccd.whatgoeson.ui.screens.common.DisplayLoading

@Composable
fun Feature1SubScreen2(
    navController: NavHostController,
    viewModel: Feature1SubScreen2ViewModel = Feature1SubScreen2ViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    DisplayContent(uiState)
}

@Composable
private fun DisplayContent(uiState: Feature1SubScreen2UiState) {
    when {
        uiState.isLoading -> DisplayLoading()
        uiState.error != null -> DisplayError(uiState.error)
        uiState.data != null -> DisplayData(uiState.data)
    }
}



@Composable
fun DisplayData(data: Any) {

}
