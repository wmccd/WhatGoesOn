package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels

import com.google.gson.annotations.SerializedName

data class AlbumInformationModel (
  @SerializedName("album" ) var album : Album? = Album()
)