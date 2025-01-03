package com.wmccd.whatgoeson.repository.webservice

import com.wmccd.whatgoeson.BuildConfig
import com.wmccd.whatgoeson.repository.webservice.responsemodels.ArtistSearchResult
import retrofit2.Response //// Ensure you're importing the correct Response class
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

//https://publicapis.dev/category/music

interface SetListFmApi {
    @GET("search/artists")
    suspend fun searchArtists(
        @Query("artistName") artistName: String,
        @Query("p") page: Int = 1,
        @Query("sort") sort: String = "relevance",
        @Header("x-api-key") apiKey: String = BuildConfig.SETLIST_FM_API_KEY,
        @Header("Accept") format: String = "application/json"
    ): Response<ArtistSearchResult>

    companion object {
        const val BASE_URL = "https://api.setlist.fm/rest/1.0/"
    }
}