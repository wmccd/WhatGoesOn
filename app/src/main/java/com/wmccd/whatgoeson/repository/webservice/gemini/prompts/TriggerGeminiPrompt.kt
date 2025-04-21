package com.wmccd.whatgoeson.repository.webservice.gemini.prompts

import GeminiApiClient
import android.util.Log
import com.google.gson.JsonSyntaxException
import com.wmccd.whatgoeson.BuildConfig
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.repository.webservice.gemini.common.GeminiApiFailureException
import com.wmccd.whatgoeson.repository.webservice.gemini.common.GeminiEmptyResponseException
import com.wmccd.whatgoeson.repository.webservice.gemini.common.GeminiNetworkException
import com.wmccd.whatgoeson.repository.webservice.gemini.common.GeminiParsingException
import com.wmccd.whatgoeson.repository.webservice.gemini.common.GeminiRequest
import com.wmccd.whatgoeson.repository.webservice.gemini.common.GeminiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class TriggerGeminiPrompt {

    fun trigger(
        promptType: PromptType,
        prompt: String,
        success: (Any) -> Unit,
        failure: (Exception) -> Unit
    ){
        MyApplication.utilities.logger.log(Log.INFO, TAG, "triggering prompt: $prompt")
        val request = buildRequest(prompt)

        GeminiApiClient.getGeminiApiService().generateContent(
            apiKey = BuildConfig.GEMINI_API_KEY,
            request = request
        ).enqueue(object : Callback<GeminiResponse> {
            override fun onResponse(
                call: Call<GeminiResponse>,
                response: Response<GeminiResponse>
            ) {
                handleResponse(response, promptType, success, failure)
            }

            override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                failure(GeminiNetworkException(thowable = t))
            }
        })
    }

    private fun handleResponse(
        response: Response<GeminiResponse>,
        promptType: PromptType,
        success: (Any) -> Unit,
        failure: (Exception) -> Unit
    ) {
        if (checkifSuccessful(response))
            handleSuccessfulResponse(response, promptType, success, failure)
         else
            handleFailureResponse(failure, response)
    }

    private fun handleFailureResponse(
        failure: (Exception) -> Unit,
        response: Response<GeminiResponse>
    ) {
        failure(
            GeminiApiFailureException(
                responseCode = response.code().toString(),
                responseMessage = response.message()
            )
        )
    }

    private fun handleSuccessfulResponse(
        response: Response<GeminiResponse>,
        promptType: PromptType,
        success: (Any) -> Unit,
        failure: (Exception) -> Unit
    ) {
        val responseText = extractResponse(response)
        val cleanedResponse = cleanResponse(responseText)
        cleanedResponse?.let {
            try {
                GeminiResponseHandler().successfulResponseHandler(
                    promptType,
                    cleanedResponse,
                    success
                )
            } catch (e: JsonSyntaxException) {
                failure(GeminiParsingException(thowable = e))
            }
        } ?: run {
            failure(GeminiEmptyResponseException())
        }
    }

    private fun checkifSuccessful(response: Response<GeminiResponse>) =
        response.isSuccessful && response.body()?.candidates?.isNotEmpty() == true && response.body()?.candidates?.get(
            0
        )?.content?.parts?.isNotEmpty() == true

    private fun buildRequest(prompt: String) = GeminiRequest(
        contents = listOf(
            GeminiRequest.Content(
                parts = listOf(
                    GeminiRequest.Content.Part(text = prompt)
                )
            )
        )
    )

    private fun extractResponse(response: Response<GeminiResponse>): String? {
        val geminiResponse = response.body()!!
        val responseText = geminiResponse.candidates
            ?.get(0)
            ?.content
            ?.parts
            ?.get(0)
            ?.text
        return responseText
    }

    private fun cleanResponse(responseText: String?) = responseText
        ?.replace("```json", "")
        ?.replace("```html\n", "")
        ?.replace("\n```", "")
        ?.replace("\n", "")
        ?.replace("```", "")

    companion object{
        private val TAG = this::class.java.simpleName
    }
}
