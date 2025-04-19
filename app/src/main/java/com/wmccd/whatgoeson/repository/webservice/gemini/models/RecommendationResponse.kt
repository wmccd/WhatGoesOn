package com.wmccd.whatgoeson.repository.webservice.gemini.models

data class RecommendationResponse(val recommendations: List<Recommendation>) {
    data class Recommendation(
        val album_name: String,
        val artist: String,
        val release_year: String,
        val details: String
    )
}