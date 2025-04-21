package com.wmccd.whatgoeson.presentation.screens.common.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SectionLabel(label: String) {
    Text(
        text = label,
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        fontWeight = FontWeight.Bold
    )
}