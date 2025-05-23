package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels

import com.google.gson.annotations.SerializedName


data class Background (
  @SerializedName("context"              ) var context             : String? = null,
  @SerializedName("influence"            ) var influence           : String? = null,
  @SerializedName("interesting_anecdote" ) var interestingAnecdote : String? = null
)