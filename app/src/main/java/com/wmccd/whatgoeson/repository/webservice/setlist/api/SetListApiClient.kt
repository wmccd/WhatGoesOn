package com.wmccd.whatgoeson.repository.webservice.setlist.api

import com.wmccd.whatgoeson.repository.webservice.CurlLoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SetListApiClient {

    private val interceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
            .addInterceptor(CurlLoggingInterceptor())
    }.build()

    fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SetListFmApiService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}