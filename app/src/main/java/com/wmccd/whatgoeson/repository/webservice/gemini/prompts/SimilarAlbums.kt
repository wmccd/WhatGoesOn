package com.wmccd.whatgoeson.repository.webservice.gemini.prompts

import GeminiApiClient
import android.util.Log
import com.google.gson.Gson
import com.wmccd.whatgoeson.BuildConfig
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.repository.webservice.gemini.GeminiApiFailureException
import com.wmccd.whatgoeson.repository.webservice.gemini.GeminiEmptyResponseException
import com.wmccd.whatgoeson.repository.webservice.gemini.GeminiNetworkException
import com.wmccd.whatgoeson.repository.webservice.gemini.GeminiParsingException
import com.wmccd.whatgoeson.repository.webservice.gemini.models.GeminiRequest
import com.wmccd.whatgoeson.repository.webservice.gemini.models.GeminiResponse
import com.wmccd.whatgoeson.repository.webservice.gemini.models.similaralbums.SimilarAlbumsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val apiKey = BuildConfig.GEMINI_API_KEY

class SimilarAlbums {
    fun getSimilarAlbumsDirect(
        albumName: String,
        artist: String,
        success: (List<SimilarAlbumsResponse.SimilarAlbum>) -> Unit,
        failure: (Exception) -> Unit
    ){
        var similarAlbumList: List<SimilarAlbumsResponse.SimilarAlbum> = emptyList()
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

        GeminiApiClient.getGeminiApiService().generateContent(
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
                            similarAlbumList =
                                gson.fromJson<List<SimilarAlbumsResponse.SimilarAlbum>>(
                                    it,
                                    object :
                                        com.google.gson.reflect.TypeToken<List<SimilarAlbumsResponse.SimilarAlbum>>() {}.type
                                )
                            MyApplication.utilities.logger.log(Log.INFO, TAG, "getSimilarAlbumsDirect: $similarAlbumList")
                            success(similarAlbumList)
                        } catch (e: com.google.gson.JsonSyntaxException) {
                            failure(GeminiParsingException(thowable = e))
                        }
                    } ?: run {
                        failure(GeminiEmptyResponseException())
                    }
                } else {
                    failure(GeminiApiFailureException(responseCode = response.code().toString(), responseMessage = response.message()) )
                }
            }

            override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                failure(GeminiNetworkException(thowable = t))
            }
        })
    }

    companion object{
        private val TAG = this::class.java.simpleName
    }
}
