package com.wmccd.whatgoeson.repository.webservice

import com.wmccd.whatgoeson.repository.webservice.responsemodels.ArtistSearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicBrainzApi {

    @GET("artist")
    suspend fun searchArtists(
        @Query("query") artistName: String,
        @Query("fmt") format: String = "json",
        @Query("limit") limit: Int = 1,
    ): Response<ArtistSearchResult>

    companion object {
        const val BASE_URL = "https://musicbrainz.org/ws/2/"
    }
}