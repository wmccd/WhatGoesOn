package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation

import com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels.AlbumInformationModel

data class AlbumInformationPromptModel(
    val albumName: String,
    val artist: String,
    val success: (AlbumInformationModel) -> Unit,
    val failure: (Exception) -> Unit
)