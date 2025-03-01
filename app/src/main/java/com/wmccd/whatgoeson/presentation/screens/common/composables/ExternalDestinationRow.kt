package com.wmccd.whatgoeson.presentation.screens.common.composables

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.presentation.screens.common.PreviewTheme
import com.wmccd.whatgoeson.utility.musicPlayer.MusicPlayer
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ExternalDestinationRow(
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
        DisplaySiteDestinationIcon(context, albumName, artistName, R.drawable.official, R.string.official_website)

        if (spotifyEnabled) {
            DisplayPlayerDestinationIcon(MusicPlayer.SPOTIFY, onSpotifyTapped)
        }
        if (youTubeMusicEnabled) {
            DisplayPlayerDestinationIcon(MusicPlayer.YOUTUBE_MUSIC, onYouTubeMusicTapped)
        }
    }
}

@Composable
private fun DisplaySiteDestinationIcon(
    context: Context,
    albumName: String,
    artistName: String,
    @DrawableRes drawableId: Int,
    @StringRes siteId: Int
) {
    val site = stringResource(siteId)
    IconButton(
        onClick = {
            openCustomTab(
                context = context,
                albumName = albumName,
                artistName = artistName,
                site = site
            )
        }
    ) {
        DrawLogo(drawableId)
    }
}

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

@Composable
private fun DrawLogo(drawableId: Int) {
    Image(
        painter = painterResource(drawableId),
        contentDescription = null,
        modifier = Modifier
            .height(24.dp)
            .width(24.dp)
    )
}


fun openCustomTab(
    context: Context,
    albumName: String,
    artistName: String,
    site: String
) {
    val searchQuery = "$site ${artistName} ${albumName}"
    val encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString())

    val searchUrl = if(site == "YouTube")
        "https://www.youtube.com/results?search_query=$encodedQuery"
    else
        "https://www.google.com/search?q=$encodedQuery"

    // 1. Create a Custom Tab Intent Builder
    val builder = CustomTabsIntent.Builder()

    // 2. Build the CustomTabsIntent
    val customTabsIntent = builder.build()

    // 3. Add the FLAG_ACTIVITY_NEW_TASK flag directly to the CustomTabsIntent's intent
    customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    customTabsIntent.intent.data = Uri.parse(searchUrl)

    // 4. Launch the Custom Tab with the URL
    customTabsIntent.launchUrl(context, Uri.parse(searchUrl))
}

@Preview(showBackground = true)
@Composable
fun ExternalDestinationRowPreview(){
    PreviewTheme {
        ExternalDestinationRow(
            albumName = "Abbey Road",
            artistName = "The Beatles",
            spotifyEnabled = true,
            youTubeMusicEnabled = true,
            onSpotifyTapped = {},
            onYouTubeMusicTapped = {}
        )
    }
}
