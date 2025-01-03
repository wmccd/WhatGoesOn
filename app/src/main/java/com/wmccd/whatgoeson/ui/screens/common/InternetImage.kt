package com.wmccd.whatgoeson.ui.screens.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.wmccd.whatgoeson.ui.theme.WhatGoesOnTheme

@Composable
fun InternetImage(
    imageUrl: String,
    imageSize: Int = 300,
){
    val placeholder: Painter = rememberVectorPainter(image = Icons.Filled.Refresh)
    val error: Painter = rememberVectorPainter(image = Icons.Filled.Warning)

    AsyncImage(
        model = imageUrl,
        contentDescription = "Image description",
        placeholder = placeholder,
        error = error,
        modifier = Modifier.size(imageSize.dp)
    )
}

@Composable
fun InternetImage(
    imageUrl: String,
){
    val placeholder: Painter = rememberVectorPainter(image = Icons.Filled.Refresh)
    val error: Painter = rememberVectorPainter(image = Icons.Filled.Warning)

    AsyncImage(
        model = imageUrl,
        contentDescription = "Image description",
        placeholder = placeholder,
        error = error,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun PreviewInternetImage100(){
    WhatGoesOnTheme {
        InternetImage(
            imageUrl = "https://superdeluxeedition.com/wp-content/uploads/2024/12/SDE_BSOTY.jpg",
            imageSize = 100
        )
    }
}

@Preview
@Composable
private fun PreviewInternetImageFillMaxWidth(){
    WhatGoesOnTheme {
        InternetImage(
            imageUrl = "https://superdeluxeedition.com/wp-content/uploads/2024/12/SDE_BSOTY.jpg",
        )
    }
}