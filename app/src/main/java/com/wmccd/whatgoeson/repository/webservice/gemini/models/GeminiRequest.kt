package com.wmccd.whatgoeson.repository.webservice.gemini.models

// Java
class GeminiRequest(val contents: List<Content>) {
    class Content(val parts: List<Part>) {
        class Part(val text: String)
    }
}