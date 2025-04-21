package com.wmccd.whatgoeson.repository.webservice.gemini.models.albuminformation

import com.google.gson.annotations.SerializedName

data class Tracks (
  @SerializedName("title"    ) var title    : String? = null,
  @SerializedName("duration" ) var duration : String? = null
)