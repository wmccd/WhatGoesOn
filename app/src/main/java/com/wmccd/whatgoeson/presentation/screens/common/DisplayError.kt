package com.wmccd.whatgoeson.presentation.screens.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wmccd.whatgoeson.presentation.theme.MyAppTheme

@Composable
fun DisplayError(error: String? = null) {
    //Displays an error message if an error occurs. Use anywhere in the app
    Box(
        modifier = Modifier.fillMaxSize().padding(STANDARD_SCREEN_PADDING),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector =  Icons.Default.Warning,
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.error
            )
            Text("Uh-oh. Something went wrong.")
            if(error != null) {
                Text(error)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DisplayErrorPreview() {
    MyAppTheme(
        darkTheme = false
    ) {
        DisplayError("This is an error message")
    }
}