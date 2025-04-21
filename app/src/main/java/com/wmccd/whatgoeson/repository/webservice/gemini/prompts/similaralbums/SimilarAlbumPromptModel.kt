package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.similaralbums

import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.similaralbums.responsemodels.SimilarAlbumsResponse

data class SimilarAlbumPromptModel(
    val albumName: String,
    val artist: String,
    val success: (List<SimilarAlbumsResponse.SimilarAlbum>) -> Unit,
    val failure: (Exception) -> Unit
)