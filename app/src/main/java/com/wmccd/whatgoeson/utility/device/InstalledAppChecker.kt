package com.wmccd.whatgoeson.utility.device

import android.content.pm.PackageManager
import com.wmccd.whatgoeson.MyApplication

class InstalledAppChecker {
    private val packageManager: PackageManager = MyApplication.appContext.packageManager

    fun check(appPackage: AppPackage) = try {
        packageManager.getPackageInfo(appPackage.appId, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

    enum class AppPackage (val appId: String){
        SPOTIFY(appId = SPOTIFY_PACKAGE_ID),
        YOUTUBE_MUSIC(appId =  YOUTUBE_MUSIC_PACKAGE_ID)
    }

    companion object{
        private const val SPOTIFY_PACKAGE_ID = "com.spotify.music"
        private const val YOUTUBE_MUSIC_PACKAGE_ID = "com.google.android.apps.youtube.music"
    }
}