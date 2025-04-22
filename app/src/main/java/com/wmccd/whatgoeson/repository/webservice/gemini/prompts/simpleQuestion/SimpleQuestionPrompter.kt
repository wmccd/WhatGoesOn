package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.BasePrompter
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.BaseSuccessHandler
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.PromptType
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.TriggerGeminiPrompt
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion.responsemodels.SimpleQuestionResponse

class SimpleQuestionPrompter:
    BasePrompter<SimpleQuestionPromptModel>,
    BaseSuccessHandler<SimpleQuestionResponse> {

    override fun prompt(promptModel: SimpleQuestionPromptModel){
        val prompt = buildPrompt(promptModel)
        TriggerGeminiPrompt().trigger (
            promptType = PromptType.SIMPLE_QUESTION,
            prompt = prompt,
            success = promptModel.success as (Any) -> Unit,
            failure = promptModel.failure
        )
    }

    private fun buildPrompt(promptModel: SimpleQuestionPromptModel): String {

        val conditionals = when (promptModel.questionType) {
            SimpleQuestionPromptType.HIGHLY_RATED_FIVE_YEARS -> {
                SimpleQuestionConditionalType.ENGLISH_ONLY.condition +
                        SimpleQuestionConditionalType.SOME_OBSCURE_CHOICES.condition
            }

            else -> ""
        } + SimpleQuestionConditionalType.JSON_FORMAT.condition + SimpleQuestionConditionalType.NO_EXTRANEOUS_CHARACTERS.condition

        return (promptModel.questionType.prompt + conditionals).replace(ARTISTS, promptModel.artistNames?.joinToString(",") ?: "")
    }

    override fun handleSuccess(
        it: String,
        success: (SimpleQuestionResponse) -> Unit
    ) {
        val result: SimpleQuestionResponse
        val gson = Gson()
        result = gson.fromJson(it, object : TypeToken<SimpleQuestionResponse>() {}.type)
        MyApplication.utilities.logger.log(Log.INFO, TAG, "handleSimpleQuestionResponseSuccess: $result")
        success(result)
    }

    companion object{
        private val TAG = this::class.java.simpleName
    }
}

