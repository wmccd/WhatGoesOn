package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels

import com.google.gson.annotations.SerializedName


data class RecordingDetails (
  @SerializedName("location" ) var location : String? = null,
  @SerializedName("dates"    ) var dates    : String? = null,
  @SerializedName("notes"    ) var notes    : String? = null
)