package com.wmccd.whatgoeson.repository.webservice.gemini.common

class GeminiParsingException(message: String = "Error parsing Gemini response", thowable: Throwable? = null): Exception(message, thowable)
class GeminiEmptyResponseException(message: String = "Empty Gemini response", thowable: Throwable? = null): Exception(message, thowable)
class GeminiApiFailureException(message: String = "Empty Gemini response", responseCode: String, responseMessage: String, thowable: Throwable? = null): Exception("$message | $responseCode | $responseMessage", thowable)
class GeminiNetworkException(message: String = "Network error", thowable: Throwable? = null): Exception(message, thowable)


