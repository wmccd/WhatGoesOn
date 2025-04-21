package com.wmccd.whatgoeson.repository.webservice.gemini.prompts

import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.AlbumInformationPrompter
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.similaralbums.SimilarAlbumPrompter
import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion.SimpleQuestionPrompter

internal class GeminiResponseHandler {
    fun successfulResponseHandler(
        promptType: PromptType,
        it: String,
        success: (Any) -> Unit
    ) {
        when (promptType) {
            PromptType.SIMILAR_ALBUMS -> {
                SimilarAlbumPrompter().handleSuccess(it, success)
            }

            PromptType.ALBUM_INFORMATION -> {
                AlbumInformationPrompter().handleSuccess(it, success)
            }

            PromptType.SIMPLE_QUESTION -> {
                SimpleQuestionPrompter().handleSuccess(it, success)
            }
        }
    }
}