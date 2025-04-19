package com.wmccd.whatgoeson.repository.webservice.gemini.prompts

import com.google.gson.Gson
import com.wmccd.whatgoeson.BuildConfig
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
        artist: String
    ): List<RecommendationResponse.Recommendation> {
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
                            recommendationList
                        } catch (e: com.google.gson.JsonSyntaxException) {
                            throw RepositoryException("Error parsing Gemini response: ${e.message}")
                        }
                    } ?: run {
                        throw RepositoryException("Empty Gemini response")
                    }
                } else {
                    RepositoryException("Gemini API call failed: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                throw RepositoryException("Network error: ${t.message}")
            }
        })
        return recommendationList
    }
}
