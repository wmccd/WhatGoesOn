package com.wmccd.whatgoeson.repository.webservice.responsemodels

import com.google.gson.annotations.SerializedName

data class ArtistSearchResult (
    @SerializedName("type") var type: String? = null,
    @SerializedName("itemsPerPage") var itemsPerPage : Int? = null,
    @SerializedName("page") var page: Int? = null,
    @SerializedName("total") var total: Int? = null,
    @SerializedName("artist") var artist: ArrayList<Artist> = arrayListOf()
)

data class Artist (
    @SerializedName("mbid") var mbid: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("sortName") var sortName: String? = null,
    @SerializedName("disambiguation") var disambiguation: String? = null,
    @SerializedName("url") var url: String? = null
)