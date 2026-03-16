package com.example.velora.ui.tokens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val SoftLight = lightColorScheme(
    primary = Color(0xFFFF7A2F),
    secondary = Color(0xFF111111),
    background = Color(0xFFF4F3EF),
    surface = Color(0xFFF8F7F3),
    onPrimary = Color.White,
    onBackground = Color(0xFF111111),
    onSurface = Color(0xFF111111),
    outline = Color(0xFFE5E3DD)
)

private val SoftDark = darkColorScheme(
    primary = Color(0xFFFF9A5C),
    secondary = Color(0xFFF3F1EC),
    background = Color(0xFF111111),
    surface = Color(0xFF1A1A1A),
    onPrimary = Color.White,
    onBackground = Color(0xFFF3F1EC),
    onSurface = Color(0xFFF3F1EC),
    outline = Color(0xFF343434)
)

@Composable
fun VeloraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalVeloraTokens provides VeloraTokens()) {
        MaterialTheme(
            colorScheme = if (darkTheme) SoftDark else SoftLight,
            typography = MaterialTheme.typography,
            content = content
        )
    }
}