package com.wmccd.whatgoeson.presentation.screens.common.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme

@Composable
fun NoAlbums() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center

    ) {
        Text(
            text = stringResource(id = R.string.it_looks_like_you_are_new_here),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun PreviewNoAlbums() {
    PreviewTheme {
        NoAlbums()
    }
}