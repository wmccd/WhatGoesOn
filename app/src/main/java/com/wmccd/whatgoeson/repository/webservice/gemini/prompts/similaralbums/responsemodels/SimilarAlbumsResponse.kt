package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.similaralbums.responsemodels

data class SimilarAlbumsResponse(val similarAlbums: List<SimilarAlbum>) {
    data class SimilarAlbum(
        val album_name: String,
        val artist: String,
        val release_year: String,
        val details: String
    )
}