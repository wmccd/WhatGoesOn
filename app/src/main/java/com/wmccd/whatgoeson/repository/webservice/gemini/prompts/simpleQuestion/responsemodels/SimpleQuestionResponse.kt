package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion.responsemodels

import com.google.gson.annotations.SerializedName

data class SimpleQuestionResponse (

  @SerializedName("overview" ) var overview : String?            = null,
  @SerializedName("details"  ) var details  : ArrayList<Details> = arrayListOf(),
  @SerializedName("summary"  ) var summary  : String?            = null

)