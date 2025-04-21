package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels

import com.google.gson.annotations.SerializedName


data class Sources (
  @SerializedName("source"         ) var source        : String? = null,
  @SerializedName("rating"         ) var rating        : String? = null,
  @SerializedName("review_snippet" ) var reviewSnippet : String? = null
)