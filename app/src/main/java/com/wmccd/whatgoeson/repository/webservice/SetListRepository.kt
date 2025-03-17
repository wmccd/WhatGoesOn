package com.wmccd.whatgoeson.repository.webservice

import com.wmccd.whatgoeson.repository.webservice.responsemodels.ArtistSearchResult
import retrofit2.Response

class SetListFmRepository {
    private val setListFmApi = RetrofitInstance.getRetrofitInstance().create(SetListFmApi::class.java)

    suspend fun searchArtists(artistName: String): Response<ArtistSearchResult> {
        return setListFmApi.searchArtists(artistName = artistName)
    }
}