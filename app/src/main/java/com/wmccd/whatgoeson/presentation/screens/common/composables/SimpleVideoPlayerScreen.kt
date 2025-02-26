package com.wmccd.whatgoeson.presentation.screens.common.composables

import android.net.Uri
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.annotation.RawRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.wmccd.whatgoeson.R

@Composable
fun SimpleVideoPlayerScreen(
    @RawRes videoResId: Int,
    onEvent: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(42.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                modifier = Modifier.clickable {
                    onEvent()
                }
            )
        }
        AndroidView(
            factory = { context ->
                VideoView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    val videoUri =
                        Uri.parse("android.resource://" + context.packageName + "/" + videoResId)
                    setVideoURI(videoUri)

                    val mediaController = MediaController(context)
                    setMediaController(mediaController)
                    mediaController.setAnchorView(this)

                    setOnPreparedListener {
                        start()
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}