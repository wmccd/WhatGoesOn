package com.wmccd.whatgoeson.repository.webservice.setlist

import com.wmccd.whatgoeson.repository.webservice.setlist.api.SetListApiClient
import com.wmccd.whatgoeson.repository.webservice.setlist.api.SetListFmApiService
import com.wmccd.whatgoeson.repository.webservice.setlist.responsemodels.ArtistSearchResult
import retrofit2.Response

class SetListFmRepository {
    private val setListFmApiService = SetListApiClient.getRetrofitInstance().create(
        SetListFmApiService::class.java)

    suspend fun searchArtists(artistName: String): Response<ArtistSearchResult> {
        return setListFmApiService.searchArtists(artistName = artistName)
    }
}