package com.wmccd.whatgoeson.presentation.screens.common.composables

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat

@Composable
fun HtmlTextView(html: String, modifier: Modifier = Modifier) {
    val lc = LocalContext.current
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                setTextColor( if(isSystemInDarkTheme(lc))
                        Color.WHITE
                    else
                        Color.BLACK
                )
                text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        },
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        },
        modifier = modifier
    )
}


@Composable
fun TextViewInAndroidView(
    html: String
) {
    val lc = LocalContext.current
    // Use remember to ensure the TextView is only created once
//    val textView = remember {
//        TextView(lc).apply {
//            textSize = 20f
//            // Set a default text color
//            setTextColor(ContextCompat.getColor(context, android.R.color.white))
//            text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
//        }
//    }

    // Apply a modifier to the AndroidView, and use Surface for background
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = if(isSystemInDarkTheme(lc))
            androidx.compose.ui.graphics.Color.Black
        else
            androidx.compose.ui.graphics.Color.White
    ) { // Set the background of the whole screen
        AndroidView(
            factory = {
                TextView(lc).apply {
                    text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
            },
            modifier = Modifier.fillMaxSize(), // Make the TextView fill its container
            update = { view ->
                // Update the text color dynamically based on the current theme.
                val context = view.context
                val isDarkTheme = isSystemInDarkTheme(context) //use extension
                val textColor = if (isDarkTheme) {
                    ContextCompat.getColor(context, android.R.color.white)
                } else {
                    ContextCompat.getColor(context, android.R.color.black)
                }
                view.setTextColor(textColor)
            }
        )
    }
}

fun String.removeCharsUpToLastClosingBrace(): String {
    val lastIndex = lastIndexOf('}')
    return if (lastIndex != -1) {
        substring(lastIndex + 1)
    } else {
        this // Return the original string if no '}' is found
    }
}
fun isSystemInDarkTheme(context: Context): Boolean {
    val configuration = context.resources.configuration
    val uiMode = configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
    return uiMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
}