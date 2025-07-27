package com.nutritrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

class NutriTrackTheme {
    @Composable
    fun NutriTrackTheme(content: @Composable () -> Unit) {
        MaterialTheme(
            colorScheme = lightColorScheme(),
        typography = Typography,
        content = content
        )
    }

}