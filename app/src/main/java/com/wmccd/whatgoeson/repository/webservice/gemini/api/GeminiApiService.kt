package com.wmccd.whatgoeson.repository.webservice.gemini.api

import com.wmccd.whatgoeson.repository.webservice.gemini.common.GeminiRequest
import com.wmccd.whatgoeson.repository.webservice.gemini.common.GeminiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    fun generateContent(
        @Query("key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: GeminiRequest,
    ): Call<GeminiResponse>
}