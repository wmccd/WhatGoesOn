package com.wmccd.whatgoeson.repository.webservice

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AddContentTypeOnEmptyBodyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        if (originalRequest.method == "POST" || originalRequest.method == "PUT" || originalRequest.method == "PATCH") {
            if (originalRequest.body?.contentLength() == 0L || originalRequest.body == null) {
                // Add the Content-Type header if it's not already present
                if (originalRequest.header("Content-Type") == null) {
                    requestBuilder.addHeader("Content-Type", "application/json") // Or your desired content type
                }
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}