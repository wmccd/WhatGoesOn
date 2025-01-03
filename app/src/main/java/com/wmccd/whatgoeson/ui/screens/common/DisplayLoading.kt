package com.wmccd.whatgoeson.ui.screens.common

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.R
import com.wmccd.whatgoeson.ui.theme.WhatGoesOnTheme

@Composable
fun DisplayLoading() {
    val context = LocalContext.current
    //Displays a loading spinner if data is being loaded. Use anywhere in the app
    MyApplication.utilities.logger.log(Log.INFO, "DisplayLoading", "DisplayLoading")
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text(stringResource(R.string.loading, context))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DisplayLoadingPreview() {
    WhatGoesOnTheme {
        DisplayLoading()
    }
}
