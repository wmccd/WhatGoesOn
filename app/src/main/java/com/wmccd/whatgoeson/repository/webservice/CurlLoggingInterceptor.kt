package com.wmccd.whatgoeson.repository.webservice

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class CurlLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        val curlCommand = StringBuilder().apply {
            append("curl -X ${request.method}")

            for ((name, value) in request.headers) {
                append(" -H \"$name: $value\"")
            }

            request.body?.let { requestBody ->
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                val charset = requestBody.contentType()?.charset() ?: Charset.forName("UTF-8")
                val requestBodyString = buffer.readString(charset)
                append(" -d '$requestBodyString'")
            }

            append(" \"${request.url}\"")
        }.toString()

        println("Generated Curl: $curlCommand") // Or use your preferred logging mechanism

        val startNs = System.nanoTime()
        val response: Response = chain.proceed(request)
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        println("<-- ${response.code} ${response.message} (${tookMs}ms)")

        return response
    }
}