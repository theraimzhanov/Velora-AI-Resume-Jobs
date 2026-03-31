package com.velora.mobile.ui.tokens

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
    surfaceVariant = Color(0xFFEDEAE3),

    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF111111),
    onSurface = Color(0xFF111111),
    onSurfaceVariant = Color(0xFF5F5B57),

    outline = Color(0xFFE5E3DD),
    error = Color(0xFFD64545)
)

private val SoftDark = darkColorScheme(
    primary = Color(0xFFFF9B62),
    secondary = Color(0xFFF4F3EF),

    background = Color(0xFF111315),
    surface = Color(0xFF191C1F),
    surfaceVariant = Color(0xFF23272B),

    onPrimary = Color.White,
    onSecondary = Color(0xFF111111),
    onBackground = Color(0xFFF4F3EF),
    onSurface = Color(0xFFF4F3EF),
    onSurfaceVariant = Color(0xFFC7C9CC),

    outline = Color(0xFF34383D),
    error = Color(0xFFFF8A80)
)

@Composable
fun VeloraTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()

    CompositionLocalProvider(LocalVeloraTokens provides VeloraTokens()) {
        MaterialTheme(
            colorScheme = if (darkTheme) SoftDark else SoftLight,
            typography = MaterialTheme.typography,
            content = content
        )
    }
}