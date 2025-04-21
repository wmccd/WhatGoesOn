package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.similaralbums

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.BasePrompter
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.BaseSuccessHandler
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.PromptType
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.TriggerGeminiPrompt
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.similaralbums.responsemodels.SimilarAlbumsResponse

class SimilarAlbumPrompter:
    BasePrompter<SimilarAlbumPromptModel>,
    BaseSuccessHandler<List<SimilarAlbumsResponse.SimilarAlbum>> {

    override fun prompt(promptModel: SimilarAlbumPromptModel){
        TriggerGeminiPrompt().trigger (
            promptType = PromptType.SIMILAR_ALBUMS,
            prompt = "Recommend 5 albums similar to '${promptModel.albumName}' by ${promptModel.artist}. Return the recommendations as a JSON array with fields 'album_name', 'artist', 'release_year', and 'details' in a flat structure.",
            success = promptModel.success as (Any) -> Unit,
            failure = promptModel.failure
        )
    }

    override fun handleSuccess(
        it: String,
        success: (List<SimilarAlbumsResponse.SimilarAlbum>) -> Unit
    ) {
        val result: List<SimilarAlbumsResponse.SimilarAlbum>
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

    companion object{
        private val TAG = this::class.java.simpleName
    }
}

