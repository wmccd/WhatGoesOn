package com.wmccd.whatgoeson.utility.musicPlayer

import androidx.annotation.DrawableRes
import com.wmccd.whatgoeson.R

enum class MusicPlayer(@DrawableRes val imageId: Int) {
    SPOTIFY(imageId = R.drawable.logo_spotify),
    YOUTUBE_MUSIC(imageId = R.drawable.logo_youtubemusic)
}