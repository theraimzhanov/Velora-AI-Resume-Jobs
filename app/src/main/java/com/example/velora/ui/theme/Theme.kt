package com.example.velora.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Dark = darkColorScheme(
    primary = Color(0xFF7C5CFF),
    secondary = Color(0xFF2DE2E6),
    background = Color(0xFF070812),
    surface = Color(0xFF0F1223),
    onPrimary = Color.White,
    onBackground = Color(0xFFECECF7),
    onSurface = Color(0xFFECECF7)
)

@Composable
fun VeloraTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Dark, typography = Typography(), content = content)
}