package com.wmccd.whatgoeson.presentation.screens.common.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme

const val INTERNET_IMAGE_NOT_AVAILABLE = "N/A"

@Composable
fun MyInternetImage(
    imageUrl: String,
    imageSize: Int = 300,
    modifier: Modifier = Modifier,
    successful: (Boolean) -> Unit = {},
){
    if(imageUrl.isEmpty() || imageUrl == INTERNET_IMAGE_NOT_AVAILABLE) {
        successful(false)
        return
    }

    val placeholder: Painter = rememberVectorPainter(image = Icons.Filled.Refresh)
    val error: Painter = rememberVectorPainter(image = Icons.Filled.Refresh)
    AsyncImage(
        model = imageUrl,
        contentDescription = "Image description",
        placeholder = placeholder,
        error = error,
        modifier = modifier.size(imageSize.dp).then(modifier),
        onSuccess = {
            successful(true)
        },
        onError = {
            successful(false)
        }
    )
}

@Preview
@Composable
private fun PreviewMyInternetImage100(){
    PreviewTheme {
        MyInternetImage(
            imageUrl = "https://superdeluxeedition.com/wp-content/uploads/2024/12/SDE_BSOTY.jpg",
            imageSize = 100
        )
    }
}

@Preview
@Composable
private fun PreviewInternetImageFillMaxWidth(){
    PreviewTheme {
        MyInternetImage(
            imageUrl = "https://superdeluxeedition.com/wp-content/uploads/2024/12/SDE_BSOTY.jpg",
        )
    }
}