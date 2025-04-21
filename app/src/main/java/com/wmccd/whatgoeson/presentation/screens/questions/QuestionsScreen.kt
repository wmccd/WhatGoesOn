package com.wmccd.whatgoeson.presentation.screens.questions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.presentation.screens.common.composables.SectionLabel
import com.wmccd.whatgoeson.presentation.screens.common.composables.SectionTitle
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayError
import com.wmccd.whatgoeson.presentation.screens.common.screens.DisplayLoading
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion.SimpleQuestionPromptType
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion.responsemodels.SimpleQuestionResponse

@Composable
fun QuestionsScreen(
    navController: NavHostController,
    viewModel: QuestionsViewModel = QuestionsViewModel()
) {

    // Listen for navigation events sent by the ViewModel
//    LaunchedEffect(key1 = UUID.randomUUID().toString()) {
//        viewModel.navigationEvent.collect { event ->
//            when (event) {
//                is NavigationEvent.NavigateToNextScreen -> {
//                    //TODO Add Destination Route
//                    navController.navigate(NavigationEnum.AddDestination.route)
//                }
//            }
//        }
//    }
    DisplayContentMode(viewModel)
}

@Composable
private fun DisplayContentMode(viewModel: QuestionsViewModel) {
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
    uiState: QuestionsUiState,
    onEvent: (QuestionsEvents) -> Unit = {},
) {
    if (uiState.data == null) {
        DisplayError(stringResource(R.string.no_data_to_display))
    } else {
        DisplayData(uiState.data, onEvent)
    }
}

@Composable
fun DisplayData(
    data: QuestionsUiData,
    onEvent: (QuestionsEvents) -> Unit = {},
) {

    Column(
        modifier = Modifier.padding( horizontal = 16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Questions(data, onEvent)
        Button(
            onClick = {
                onEvent(QuestionsEvents.SubmitClicked)
            }
        ) {
            Text(text = "Submit")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if(data.processing) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Consulting the Oracle for you...")
        }else{
            DisplayResponse(data.simpleQuestionResponse)
        }
    }
}

@Composable
fun DisplayResponse(
    simpleQuestionResponse: SimpleQuestionResponse?
) {
    if(simpleQuestionResponse == null){
        return
    }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState).padding( horizontal = 16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionTitle(title = "Overview")
        Text(text = simpleQuestionResponse.overview.orEmpty())

        SectionTitle(title = "Details")
        simpleQuestionResponse.details.forEach {
            SectionLabel(it.label.orEmpty())
            Text(text = it.body.orEmpty())
        }

        SectionTitle(title = "Summary")
        Text(text = simpleQuestionResponse.summary.orEmpty())
    }
}

@Composable
private fun Questions(
    data: QuestionsUiData,
    onEvent: (QuestionsEvents) -> Unit
) {
    DropdownList(
        label = "Question",
        options = data.questionTypes?.map { it.onScreen } ?: listOf(),
        selectedOption = data.selectedQuestionPromptType?.onScreen ?: "",
    ) { newSelection ->
        val enum = SimpleQuestionPromptType.entries.find { it.onScreen == newSelection }
            ?: SimpleQuestionPromptType.HIGHLY_RATED_FIVE_YEARS
        onEvent(
            QuestionsEvents.QuestionSelected(enum)
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    DropdownList(
        label = "Artist",
        options = data.artistNames.orEmpty(),
        selectedOption = data.selectedArtist.orEmpty(),
    ) { newSelection ->
        onEvent(QuestionsEvents.ArtistSelected(newSelection))
    }
    Spacer(modifier = Modifier.height(16.dp))
}


@Composable
fun DropdownList(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String)->Unit
) {
    // State to control the expanded state of the dropdown
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center).padding(horizontal = 16.dp) // Center the content
    ) {
        // Box to hold the dropdown components
        Box {
            // OutlinedTextField to display the selected item and trigger the dropdown
            OutlinedTextField(
                value = selectedOption, // Display the selected item
                onValueChange = { /* Do nothing, it's a read-only field */ },
                modifier = Modifier.fillMaxWidth(), // Set the width of the text field
                label = { Text(label) }, // Label for the text field
                trailingIcon = { // Add an icon to the end of the text field
                    Icon(
                        Icons.Filled.ArrowDropDown, "Arrow",
                        Modifier.clickable { expanded = !expanded }) // Toggle dropdown on click
                },
                readOnly = true, // Make the text field read-only
            )

            // DropdownMenu to display the list of options
            DropdownMenu(
                expanded = expanded, // Control the visibility of the dropdown
                onDismissRequest = { expanded = false }, // Close the dropdown when dismissed
                modifier = Modifier.fillMaxWidth(), // Match the width of the text field
            ) {
                // Iterate over the options and create a DropdownMenuItem for each
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(option) // Display the option text
                        },
                        onClick = { // Handle item selection
                            onOptionSelected(option)
                            expanded = false // Close the dropdown
                        }
                    )
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
            uiState = QuestionsUiState(
                data = QuestionsUiData(
                    someData = "Hello"
                ),
            )
        )
    }
}