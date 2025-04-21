package com.wmccd.whatgoeson.repository.webservice.gemini.models.albuminformation

import com.google.gson.annotations.SerializedName

data class AlbumInformationModel (
  @SerializedName("album" ) var album : Album? = Album()
)