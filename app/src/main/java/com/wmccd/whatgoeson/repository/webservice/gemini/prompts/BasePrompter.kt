package com.wmccd.whatgoeson.repository.webservice.gemini.prompts

interface BasePrompter<T> {
    fun prompt(promptModel: T)
}


