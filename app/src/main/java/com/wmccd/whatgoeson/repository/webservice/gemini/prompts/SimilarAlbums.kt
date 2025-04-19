package com.wmccd.whatgoeson.repository.webservice.gemini.prompts

import android.util.Log
import com.google.gson.Gson
import com.wmccd.whatgoeson.BuildConfig
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.repository.RepositoryException
import com.wmccd.whatgoeson.repository.webservice.gemini.models.GeminiRequest
import com.wmccd.whatgoeson.repository.webservice.gemini.models.GeminiResponse
import com.wmccd.whatgoeson.repository.webservice.gemini.models.RecommendationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val apiKey = BuildConfig.GEMINI_API_KEY

class SimilarAlbums {
    fun getSimilarAlbumsDirect(
        albumName: String,
        artist: String,
        result: (List<RecommendationResponse.Recommendation>) -> Unit
    ){
        var recommendationList: List<RecommendationResponse.Recommendation> = emptyList()
        val prompt =
            "Recommend 5 albums similar to '$albumName' by $artist. Return the recommendations as a JSON array with fields 'album_name', 'artist', 'release_year', and 'details' in a flat structure. One of the recommendations should be less well known"
        val request = GeminiRequest(
            contents = listOf(
                GeminiRequest.Content(
                    parts = listOf(
                        GeminiRequest.Content.Part(text = prompt)
                    )
                )
            )
        )

        ApiClient.getGeminiApiService().generateContent(
            apiKey = apiKey,
            request = request
        ).enqueue(object : Callback<GeminiResponse> {
            override fun onResponse(
                call: Call<GeminiResponse>,
                response: Response<GeminiResponse>
            ) {
                if (response.isSuccessful && response.body()?.candidates?.isNotEmpty() == true && response.body()?.candidates?.get(
                        0
                    )?.content?.parts?.isNotEmpty() == true
                ) {
                    val geminiResponse = response.body()!!
                    val responseText =
                        geminiResponse.candidates?.get(0)?.content?.parts?.get(0)?.text?.replace("```json", "")?.replace("```", "")

                    responseText?.let {
                        try {
                            val gson = Gson()
                            recommendationList =
                                gson.fromJson<List<RecommendationResponse.Recommendation>>(
                                    it,
                                    object :
                                        com.google.gson.reflect.TypeToken<List<RecommendationResponse.Recommendation>>() {}.type
                                )
                            MyApplication.utilities.logger.log(Log.INFO, TAG, "getSimilarAlbumsDirect: $recommendationList")
                            result(recommendationList)
                        } catch (e: com.google.gson.JsonSyntaxException) {
                            result(emptyList())
                            throw RepositoryException("Error parsing Gemini response: ${e.message}")
                        }
                    } ?: run {
                        result(emptyList())
                        throw RepositoryException("Empty Gemini response")
                    }
                } else {
                    result(emptyList())
                    throw RepositoryException("Gemini API call failed: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                result(emptyList())
                throw RepositoryException("Network error: ${t.message}")
            }
        })
    }

    companion object{
        private val TAG = this::class.java.simpleName
    }
}
