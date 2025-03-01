package com.wmccd.whatgoeson.utility.musicPlayer

class MusicPlayerFactory(val musicPlayer: MusicPlayer) {
    fun create(): MusicPlayerLauncher {
        return when (musicPlayer) {
            MusicPlayer.SPOTIFY -> Spotify()
            MusicPlayer.YOUTUBE_MUSIC -> YouTubeMusic()
        }
    }
}