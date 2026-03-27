package com.velora.mobile.core.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

object Motion {
    const val Fast = 180
    const val Medium = 260
}

@OptIn(ExperimentalAnimationApi::class)
fun enterFadeSlide(): EnterTransition =
    fadeIn(animationSpec = tween(Motion.Medium)) +
            slideInVertically(animationSpec = tween(Motion.Medium)) { it / 12 }

@OptIn(ExperimentalAnimationApi::class)
fun exitFadeSlide(): ExitTransition =
    fadeOut(animationSpec = tween(Motion.Medium)) +
            slideOutVertically(animationSpec = tween(Motion.Medium)) { it / 12 }