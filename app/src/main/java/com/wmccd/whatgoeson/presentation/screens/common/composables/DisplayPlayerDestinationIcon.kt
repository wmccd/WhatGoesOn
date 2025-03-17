package com.wmccd.whatgoeson.presentation.screens.common.composables

import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.wmccd.whatgoeson.utility.musicPlayer.MusicPlayer

@Composable
fun DisplayPlayerDestinationIcon(
    musicPlayer: MusicPlayer,
    onEvent: () -> Unit
) {
    IconButton(
        onClick = {
            onEvent()
        }
    ) {
        DrawLogo(musicPlayer.imageId)
    }
}