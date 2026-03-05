package com.example.velora.ui.tokens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val SoftLight = lightColorScheme(
    primary = Color(0xFFFF7A2F),      // accent (orange)
    secondary = Color(0xFF111111),
    background = Color(0xFFF4F3EF),   // warm off-white
    surface = Color(0xFFF8F7F3),      // slightly lighter than bg
    onPrimary = Color.White,
    onBackground = Color(0xFF111111),
    onSurface = Color(0xFF111111),
    outline = Color(0xFFE5E3DD),
)

@Composable
fun VeloraTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalVeloraTokens provides VeloraTokens()) {
        MaterialTheme(
            colorScheme = SoftLight,
            typography = MaterialTheme.typography,
            content = content
        )
    }
}