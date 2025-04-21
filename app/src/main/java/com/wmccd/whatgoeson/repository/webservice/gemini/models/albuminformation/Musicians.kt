package com.wmccd.whatgoeson.repository.webservice.gemini.models.albuminformation

import com.google.gson.annotations.SerializedName


data class Musicians (
  @SerializedName("name"        ) var name        : String?           = null,
  @SerializedName("instruments" ) var instruments : ArrayList<String> = arrayListOf()
)