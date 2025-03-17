package com.wmccd.whatgoeson.presentation.screens.common.composables

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.utility.musicPlayer.MusicPlayer

@Composable
fun ExternalAlbumDestinationRow(
    albumName: String,
    artistName: String,
    spotifyEnabled: Boolean,
    youTubeMusicEnabled: Boolean,
    onSpotifyTapped: () -> Unit,
    onYouTubeMusicTapped: () -> Unit,
){
    val context = LocalContext.current

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        DisplaySiteDestinationIcon(context, albumName, artistName, R.drawable.logo_allmusic, R.string.allmusic)
        DisplaySiteDestinationIcon(context, albumName, artistName, R.drawable.logo_discog, R.string.discogs)
        DisplaySiteDestinationIcon(context, albumName, artistName, R.drawable.logo_wiki, R.string.wikipedia)
        DisplaySiteDestinationIcon(context, albumName, artistName, R.drawable.logo_youtube, R.string.youtube)
        DisplaySiteDestinationIcon(context, albumName, artistName, R.drawable.ic_stamp, R.string.official_website)

        if (spotifyEnabled) {
            DisplayPlayerDestinationIcon(MusicPlayer.SPOTIFY, onSpotifyTapped)
        }
        if (youTubeMusicEnabled) {
            DisplayPlayerDestinationIcon(MusicPlayer.YOUTUBE_MUSIC, onYouTubeMusicTapped)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExternalDestinationRowPreview(){
    PreviewTheme {
        ExternalAlbumDestinationRow(
            albumName = "Abbey Road",
            artistName = "The Beatles",
            spotifyEnabled = true,
            youTubeMusicEnabled = true,
            onSpotifyTapped = {},
            onYouTubeMusicTapped = {}
        )
    }
}
