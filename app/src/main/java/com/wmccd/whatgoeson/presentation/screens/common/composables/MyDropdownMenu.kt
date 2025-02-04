package com.wmccd.whatgoeson.presentation.screens.common.composables

import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MyDropdownMenu(
    expanded: Boolean,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    onDismissRequest: (Boolean) -> Unit,
    modifier: Modifier = Modifier
){
    val scrollState = rememberScrollState(0)

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismissRequest(false) },
        scrollState = scrollState,
        modifier = modifier
    ){
        options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                    onOptionSelected(option)
                    onDismissRequest(false)
                }
            )
        }
    }
}