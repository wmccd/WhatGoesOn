package com.wmccd.whatgoeson.presentation.screens.common.composables

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import kotlin.text.filter
import kotlin.text.isDigit

@Composable
fun MyOutlinedInputText(
    label: String,
    currentValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    numericOnly: Boolean = false
){
    if(numericOnly)
        OutlinedTextField(
            value = currentValue,
            onValueChange = {newValue ->
                val numericText = newValue.filter { it.isDigit() }
                onValueChange(numericText)
            },
            label = { Text(label) },
            modifier = modifier,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    else
        OutlinedTextField(
            value = currentValue,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = modifier,
        )
}