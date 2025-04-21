package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.BasePrompter
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.BaseSuccessHandler
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.PromptType
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.TriggerGeminiPrompt
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels.AlbumInformationModel

class  AlbumInformationPrompter:
    BasePrompter<AlbumInformationPromptModel>,
    BaseSuccessHandler<AlbumInformationModel> {

        override fun prompt(promptModel: AlbumInformationPromptModel){
        TriggerGeminiPrompt().trigger(
            promptType = PromptType.ALBUM_INFORMATION,
            prompt = "Tell me about the album ${promptModel.albumName} by ${promptModel.artist}. Return the answer in a format that matches this example: $jsonAlbumInformationFormat.",
            success = promptModel.success as (Any) -> Unit,
            failure = promptModel.failure
        )
    }

    override fun handleSuccess(it: String, success: (AlbumInformationModel) -> Unit) {
        val result: AlbumInformationModel
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
        private const val TAG = "AlbumInformationPrompt"
        private val jsonAlbumInformationFormat = "{" +
                "  \"album\": {" +
                "    \"title\": \"(Album Title)\"," +
                "    \"artist\": \"(Artist Name)\"," +
                "    \"release_date\": \"(Release Date - e.g., September 18, 1989)\"," +
                "    \"label\": \"(Record Label)\"," +
                "    \"producer\": \"(Producer Name)\"," +
                "    \"genre\": [\"(Genre 1)\", \"(Genre 2)\", \"...\"]," +
                "    \"musicians\": [" +
                "      {" +
                "        \"name\": \"(Musician Name)\"," +
                "        \"instruments\": [\"(Instrument 1)\", \"(Instrument 2)\", \"...\"]" +
                "      }," +
                "      {" +
                "        \"name\": \"(Musician Name)\"," +
                "        \"instruments\": [\"(Instrument 1)\", \"(Instrument 2)\", \"...\"]" +
                "      }," +
                "      // Additional musicians..." +
                "    ]," +
                "    \"recording_details\": {" +
                "      \"location\": \"(Recording Location)\"," +
                "      \"dates\": \"(Recording Dates)\"," +
                "      \"notes\": \"(Recording Notes)\"" +
                "    }," +
                "    \"background\": {" +
                "      \"context\": \"(Background Context)\"," +
                "      \"influence\": \"(Influences)\"," +
                "      \"interesting_anecdote\": \"(Description of artist's state of mind during recording)\"" +
                "    }," +
                "    \"tracks\": [" +
                "      {" +
                "        \"title\": \"(Track Title)\"," +
                "        \"duration\": \"(Track Duration - e.g., 3:43)\"" +
                "      }," +
                "      {" +
                "        \"title\": \"(Track Title)\"," +
                "        \"duration\": \"(Track Duration - e.g., 2:30)\"" +
                "      }," +
                "      // Additional tracks..." +
                "    ]," +
                "    \"reception\": {" +
                "      \"overall\": \"(Overall Review Summary)\"," +
                "      \"sources\": [" +
                "        {" +
                "          \"source\": \"(Review Source - e.g., AllMusic)\"," +
                "          \"rating\": \"(Rating - e.g., 4/5 or B+)\"," +
                "          \"review_snippet\": \"(Key review quote)\"" +
                "        }," +
                "        {" +
                "          \"source\": \"(Review Source - e.g., Rolling Stone)\"," +
                "          \"rating\": \"(Rating)\"," +
                "          \"critic\": \"(Reviewer Name)\"," +
                "          \"review_snippet\": \"(Key review quote)\"" +
                "        }," +
                "        // Additional reviews..." +
                "      ]," +
                "      \"legacy\": \"(Information about the album's legacy or impact)\"" +
                "    }" +
                "  }" +
                "}"
    }


}

