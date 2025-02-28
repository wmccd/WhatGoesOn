package com.wmccd.whatgoeson.utility.musicPlayer

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.utility.device.InstalledAppChecker
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class Spotify {

    private val installedAppChecker = InstalledAppChecker()

    fun open(artistName: String, albumName: String) {
        // 1. Create the Search Query
        val searchQuery = "$artistName $albumName"

        // 2. Encode the Query
        val encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString())

        // 3. Create the Spotify Search URI
        val spotifySearchUri = Uri.parse("spotify:search:$encodedQuery")

        openSpotify(spotifySearchUri)
    }


    private fun openSpotify(uri: Uri) {
        if (installedAppChecker.check(InstalledAppChecker.AppPackage.SPOTIFY)) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.appContext.startActivity(intent)
        } else {
            Toast.makeText(MyApplication.appContext, "Spotify app not found", Toast.LENGTH_SHORT).show()
        }
    }
}