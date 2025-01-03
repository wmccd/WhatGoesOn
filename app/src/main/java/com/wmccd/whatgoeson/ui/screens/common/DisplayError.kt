package com.wmccd.whatgoeson.ui.screens.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wmccd.whatgoeson.ui.theme.WhatGoesOnTheme

@Composable
fun DisplayError(error: String?) {
    //Displays an error message if an error occurs. Use anywhere in the app
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Uh-oh. Something went wrong.")
            Text(error?: "")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DisplayErrorPreview() {
    WhatGoesOnTheme(
        darkTheme = false
    ) {
        DisplayError("This is an error message")
    }
}