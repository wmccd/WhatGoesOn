package com.wmccd.whatgoeson.presentation.screens.common.composables

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wmccd.whatgoeson.utility.chromeTab.CustomTab

@Composable
fun DisplaySiteDestinationIcon(
    context: Context,
    searchCriteria: String = "",
    artistName: String,
    @DrawableRes drawableId: Int,
    @StringRes siteId: Int,
) {
    val site = stringResource(siteId)
    IconButton(
        onClick = {
            CustomTab().open(
                context = context,
                searchCriteria = searchCriteria,
                artistName = artistName,
                site = site
            )
        }
    ) {
        DrawLogo(drawableId)
    }
}