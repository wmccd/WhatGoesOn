package com.wmccd.whatgoeson.presentation.screens.common

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wmccd.whatgoeson.presentation.theme.MyAppTheme

@Composable
fun PreviewTheme(content: @Composable() (() -> Unit)) {
    MyAppTheme {
        Surface {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PreviewTheme {
        DisplayError("Bobbins")
    }
}