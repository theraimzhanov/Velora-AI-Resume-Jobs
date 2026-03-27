package com.velora.mobile.ui.tokens


import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp
)

data class Radius(
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 28.dp
)

data class Elevation(
    val card: Dp = 2.dp,
    val sheet: Dp = 8.dp
)

data class VeloraTokens(
    val spacing: Spacing = Spacing(),
    val radius: Radius = Radius(),
    val elevation: Elevation = Elevation()
)

val LocalVeloraTokens = staticCompositionLocalOf { VeloraTokens() }

object Velora {
    val tokens
        @androidx.compose.runtime.Composable get() = LocalVeloraTokens.current
}