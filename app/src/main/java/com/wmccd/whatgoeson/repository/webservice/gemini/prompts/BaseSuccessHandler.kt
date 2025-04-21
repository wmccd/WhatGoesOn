package com.wmccd.whatgoeson.repository.webservice.gemini.prompts

interface BaseSuccessHandler<T> {
    fun handleSuccess(it: String, success: (T) -> Unit)
}