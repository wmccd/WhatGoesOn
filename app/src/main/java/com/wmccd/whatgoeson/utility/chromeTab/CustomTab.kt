package com.wmccd.whatgoeson.utility.chromeTab

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class CustomTab {
    fun open(
        context: Context,
        searchCriteria: String,
        artistName: String,
        site: String
    ) {
        val searchQuery = "$site ${artistName} ${searchCriteria}"
        val encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString())

        val searchUrl = if (site == "YouTube")
            "https://www.youtube.com/results?search_query=$encodedQuery"
        else
            "https://www.google.com/search?q=$encodedQuery"

        // 1. Create a Custom Tab Intent Builder
        val builder = CustomTabsIntent.Builder()

        // 2. Build the CustomTabsIntent
        val customTabsIntent = builder.build()

        // 3. Add the FLAG_ACTIVITY_NEW_TASK flag directly to the CustomTabsIntent's intent
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.intent.data = Uri.parse(searchUrl)

        // 4. Launch the Custom Tab with the URL
        customTabsIntent.launchUrl(context, Uri.parse(searchUrl))
    }

    fun open(
        context: Context,
        site: String
    ) {
        // 1. Create a Custom Tab Intent Builder
        val builder = CustomTabsIntent.Builder()

        // 2. Build the CustomTabsIntent
        val customTabsIntent = builder.build()

        // 3. Add the FLAG_ACTIVITY_NEW_TASK flag directly to the CustomTabsIntent's intent
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.intent.data = Uri.parse(site)

        // 4. Launch the Custom Tab with the URL
        customTabsIntent.launchUrl(context, Uri.parse(site))
    }
}