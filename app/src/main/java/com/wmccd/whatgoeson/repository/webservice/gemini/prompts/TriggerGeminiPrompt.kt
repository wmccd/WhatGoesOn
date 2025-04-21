package com.wmccd.whatgoeson.repository.webservice.gemini.prompts

import GeminiApiClient
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wmccd.whatgoeson.BuildConfig
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.repository.webservice.gemini.GeminiApiFailureException
import com.wmccd.whatgoeson.repository.webservice.gemini.GeminiEmptyResponseException
import com.wmccd.whatgoeson.repository.webservice.gemini.GeminiNetworkException
import com.wmccd.whatgoeson.repository.webservice.gemini.GeminiParsingException
import com.wmccd.whatgoeson.repository.webservice.gemini.models.GeminiRequest
import com.wmccd.whatgoeson.repository.webservice.gemini.models.GeminiResponse
import com.wmccd.whatgoeson.repository.webservice.gemini.models.albuminformation.AlbumInformationModel
import com.wmccd.whatgoeson.repository.webservice.gemini.models.similaralbums.SimilarAlbumsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TriggerGeminiPrompt {

    fun similarAlbums(promptModel: SimilarAlbumPromptModel){
        trigger(
            promptType = PromptType.SIMILAR_ALBUMS,
            prompt = "Recommend 5 albums similar to '${promptModel.albumName}' by ${promptModel.artist}. Return the recommendations as a JSON array with fields 'album_name', 'artist', 'release_year', and 'details' in a flat structure.",
            success = promptModel.success as (Any) -> Unit,
            failure = promptModel.failure
        )
    }

    fun albumInformation(promptModel: AlbumInformationPromptModel){
        trigger(
            promptType = PromptType.ALBUM_INFORMATION,
            prompt = "Tell me about the album ${promptModel.albumName} by ${promptModel.artist}. Return the answer in a format that matches this example: $jsonAlbumInformationFormat.",
            success = promptModel.success as (Any) -> Unit,
            failure = promptModel.failure
        )
    }

    fun simplePrompt(promptModel: SimplePromptModel){
        trigger(
            promptType = PromptType.SIMPLE,
            prompt = promptModel.prompt,
            success = promptModel.success,
            failure = promptModel.failure
        )
    }

    private fun trigger(
        promptType: PromptType,
        prompt: String,
        success: (Any) -> Unit,
        failure: (Exception) -> Unit
    ){
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
            apiKey = BuildConfig.GEMINI_API_KEY,
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
                    val responseText = geminiResponse.candidates
                        ?.get(0)
                        ?.content
                        ?.parts
                        ?.get(0)
                        ?.text
                    val cleanedResponse  = responseText
                        ?.replace("```json", "")
                        ?.replace("```html\n", "")
                        ?.replace("\n```", "")
                        ?.replace("\n", "")
                        ?.replace("```", "")

                    cleanedResponse?.let {
                        try {
                            successfulResponseHandler(promptType, cleanedResponse, success)
                        } catch (e: com.google.gson.JsonSyntaxException) {
                            failure(GeminiParsingException(thowable = e))
                        }
                    } ?: run {
                        failure(GeminiEmptyResponseException())
                    }
                } else {
                    failure(
                        GeminiApiFailureException(
                            responseCode = response.code().toString(),
                            responseMessage = response.message()
                        )
                    )
                }
            }

            override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                failure(GeminiNetworkException(thowable = t))
            }
        })
    }

    private fun successfulResponseHandler(
        promptType: PromptType,
        it: String,
        success: (Any) -> Unit
    ) {
        when (promptType) {
            PromptType.SIMILAR_ALBUMS -> {
                handleSimilarAlbumsResponseSuccess(it, success)
            }

            PromptType.ALBUM_INFORMATION -> {
                handleAlbumInformationResponseSuccess(it, success)
            }

            PromptType.SIMPLE -> {
                success(it)
            }
        }
    }

    private fun handleSimilarAlbumsResponseSuccess(
        it: String,
        success: (List<SimilarAlbumsResponse.SimilarAlbum>) -> Unit
    ) {
        var result: List<SimilarAlbumsResponse.SimilarAlbum>
        val gson = Gson()
        result =
            gson.fromJson(
                it,
                object :
                    TypeToken<List<SimilarAlbumsResponse.SimilarAlbum>>() {}.type
            )
        MyApplication.utilities.logger.log(
            Log.INFO,
            TAG,
            "handleSimilarAlbumsResponseSuccess: $result"
        )
        success(result)
    }

    private fun handleAlbumInformationResponseSuccess(
        it: String,
        success: (AlbumInformationModel) -> Unit
    ) {
        var result: AlbumInformationModel
        val gson = Gson()
        result =
            gson.fromJson(
                it,
                object :
                    TypeToken<AlbumInformationModel>() {}.type
            )
        MyApplication.utilities.logger.log(
            Log.INFO,
            TAG,
            "handleAlbumInformationResponseSuccess: $result"
        )
        success(result)
    }


    companion object{
        private val TAG = this::class.java.simpleName

        private enum class PromptType{
            SIMILAR_ALBUMS,
            ALBUM_INFORMATION,
            SIMPLE
        }

        private val jsonAlbumInformationFormat = "{\n" +
                "  \"album\": {\n" +
                "    \"title\": \"(Album Title)\",\n" +
                "    \"artist\": \"(Artist Name)\",\n" +
                "    \"release_date\": \"(Release Date - e.g., September 18, 1989)\",\n" +
                "    \"label\": \"(Record Label)\",\n" +
                "    \"producer\": \"(Producer Name)\",\n" +
                "    \"genre\": [\"(Genre 1)\", \"(Genre 2)\", \"...\"],\n" +
                "    \"musicians\": [\n" +
                "      {\n" +
                "        \"name\": \"(Musician Name)\",\n" +
                "        \"instruments\": [\"(Instrument 1)\", \"(Instrument 2)\", \"...\"]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"(Musician Name)\",\n" +
                "        \"instruments\": [\"(Instrument 1)\", \"(Instrument 2)\", \"...\"]\n" +
                "      },\n" +
                "      // Additional musicians...\n" +
                "    ],\n" +
                "    \"recording_details\": {\n" +
                "      \"location\": \"(Recording Location)\",\n" +
                "      \"dates\": \"(Recording Dates)\",\n" +
                "      \"notes\": \"(Recording Notes)\"\n" +
                "    },\n" +
                "    \"background\": {\n" +
                "      \"context\": \"(Background Context)\",\n" +
                "      \"influence\": \"(Influences)\",\n" +
                "      \"interesting_anecdote\": \"(Description of artist's state of mind during recording)\"\n" +
                "    },\n" +
                "    \"tracks\": [\n" +
                "      {\n" +
                "        \"title\": \"(Track Title)\",\n" +
                "        \"duration\": \"(Track Duration - e.g., 3:43)\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"(Track Title)\",\n" +
                "        \"duration\": \"(Track Duration - e.g., 2:30)\"\n" +
                "      },\n" +
                "      // Additional tracks...\n" +
                "    ],\n" +
                "    \"reception\": {\n" +
                "      \"overall\": \"(Overall Review Summary)\",\n" +
                "      \"sources\": [\n" +
                "        {\n" +
                "          \"source\": \"(Review Source - e.g., AllMusic)\",\n" +
                "          \"rating\": \"(Rating - e.g., 4/5 or B+)\",\n" +
                "          \"review_snippet\": \"(Key review quote)\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"source\": \"(Review Source - e.g., Rolling Stone)\",\n" +
                "          \"rating\": \"(Rating)\",\n" +
                "          \"critic\": \"(Reviewer Name)\",\n" +
                "          \"review_snippet\": \"(Key review quote)\"\n" +
                "        },\n" +
                "        // Additional reviews...\n" +
                "      ],\n" +
                "      \"legacy\": \"(Information about the album's legacy or impact)\"\n" +
                "    }\n" +
                "  }\n" +
                "}\n"
    }
}

data class SimilarAlbumPromptModel(
    val albumName: String,
    val artist: String,
    val success: (List<SimilarAlbumsResponse.SimilarAlbum>) -> Unit,
    val failure: (Exception) -> Unit
)

data class AlbumInformationPromptModel(
    val albumName: String,
    val artist: String,
    val success: (AlbumInformationModel) -> Unit,
    val failure: (Exception) -> Unit
)

data class SimplePromptModel(
    val prompt: String,
    val success: (Any) -> Unit,
    val failure: (Exception) -> Unit
)