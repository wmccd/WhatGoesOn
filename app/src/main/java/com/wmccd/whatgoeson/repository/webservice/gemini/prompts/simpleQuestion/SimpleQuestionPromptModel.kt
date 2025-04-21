package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion

import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion.responsemodels.SimpleQuestionResponse

data class SimpleQuestionPromptModel(
    val questionType: SimpleQuestionPromptType,
    val artistNames: List<String>? = emptyList(),
    val success: (SimpleQuestionResponse) -> Unit,
    val failure: (Exception) -> Unit
)