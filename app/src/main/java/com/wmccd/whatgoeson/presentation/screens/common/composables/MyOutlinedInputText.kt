package com.wmccd.whatgoeson.presentation.screens.common.composables

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MyOutlinedInputText(
    label: String,
    currentValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    OutlinedTextField(
        value = currentValue,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
    )
}