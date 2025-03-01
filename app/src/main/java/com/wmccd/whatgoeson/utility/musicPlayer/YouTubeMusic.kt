package com.wmccd.whatgoeson.utility.musicPlayer

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.utility.device.InstalledAppChecker
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class YouTubeMusic: MusicPlayerLauncher {

    private val installedAppChecker = InstalledAppChecker()

    override fun launch(artistName: String, albumName: String) {
        val youTubeMusicUri = createYouTubeMusicUri(artistName, albumName)
        openYouTubeMusic(youTubeMusicUri)
    }

    private fun createYouTubeMusicUri(artistName: String, albumName: String): Uri {
        // 1. Create the Search Query
        val searchQuery = "$artistName $albumName"

        // 2. Encode the Query
        val encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString())

        // 3. Create the YouTube Music Search URI
        return Uri.parse("https://music.youtube.com/search?q=$encodedQuery")
    }

    private fun openYouTubeMusic(uri: Uri) {
        if (installedAppChecker.check(InstalledAppChecker.AppPackage.YOUTUBE_MUSIC)) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.appContext.startActivity(intent)
        } else {
            Toast.makeText(MyApplication.appContext, "YouTube Music app not found", Toast.LENGTH_SHORT).show()
        }
    }
}