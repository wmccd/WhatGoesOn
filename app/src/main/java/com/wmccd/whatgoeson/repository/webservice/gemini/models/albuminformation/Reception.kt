package com.wmccd.whatgoeson.repository.webservice.gemini.models.albuminformation

import com.google.gson.annotations.SerializedName


data class Reception (
  @SerializedName("overall" ) var overall : String?            = null,
  @SerializedName("sources" ) var sources : ArrayList<Sources> = arrayListOf(),
  @SerializedName("legacy"  ) var legacy  : String?            = null
)