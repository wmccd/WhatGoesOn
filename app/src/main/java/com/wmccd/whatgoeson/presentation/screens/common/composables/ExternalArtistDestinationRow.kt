package com.wmccd.whatgoeson.presentation.screens.common.composables

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
fun ExternalArtistDestinationRow(
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
    ) {

        DisplaySiteDestinationIcon(
            context = context,
            searchCriteria = "",
            artistName = artistName,
            drawableId = R.drawable.logo_setlist,
            siteId = R.string.setlistFm,
        )
        DisplaySiteDestinationIcon(
            context,
            "Albums Community Rankings",
            artistName,
            R.drawable.logo_tiermaker,
            R.string.tierMaker
        )
        DisplaySiteDestinationIcon(
            context,
            "Biography",
            artistName,
            R.drawable.logo_allmusic,
            R.string.allmusic
        )
        DisplaySiteDestinationIcon(
            context,
            "",
            artistName,
            R.drawable.logo_wiki,
            R.string.wikipedia
        )
        DisplaySiteDestinationIcon(
            context,
            "",
            artistName,
            R.drawable.logo_youtube,
            R.string.youtube
        )
        DisplaySiteDestinationIcon(
            context,
            "",
            artistName,
            R.drawable.ic_stamp,
            R.string.official_website
        )

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
fun ExternalArtistDestinationRowPreview(){
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
