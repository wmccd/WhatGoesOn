package com.wmccd.whatgoeson.presentation.screens.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun DrawLogo(drawableId: Int) {
    Image(
        painter = painterResource(drawableId),
        contentDescription = null,
        modifier = Modifier
            .height(24.dp)
            .width(24.dp)
    )
}