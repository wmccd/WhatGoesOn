package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion.responsemodels

import com.google.gson.annotations.SerializedName


data class Details (

  @SerializedName("label" ) var label : String? = null,
  @SerializedName("body"  ) var body  : String? = null

)