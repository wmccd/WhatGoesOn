package com.wmccd.whatgoeson.presentation.screens.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.wmccd.whatgoeson.R

@Composable
fun NoAlbums() {
    Text(
        text = stringResource(id = R.string.it_looks_like_you_are_new_here),
        textAlign = TextAlign.Center
    )
}