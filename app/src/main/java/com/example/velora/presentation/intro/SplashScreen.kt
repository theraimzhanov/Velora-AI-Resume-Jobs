package com.example.velora.presentation.intro

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    @DrawableRes logoRes: Int,
    slogan: String,
    onFinish: () -> Unit
) {
    // Total time: ~2 seconds
    LaunchedEffect(Unit) {
        delay(2000)
        onFinish()
    }

    // Smooth premium motion
    val ease = FastOutSlowInEasing

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 700, easing = ease),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 650, easing = ease),
        label = "alpha"
    )

    val floatAnim = rememberInfiniteTransition(label = "float").animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = ease),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    // Start at 0 then animate in
    var start by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { start = true }

    val appliedScale = if (start) 1.0f else 0.85f
    val appliedAlpha = if (start) 1.0f else 0.0f

    val logoScale by animateFloatAsState(
        targetValue = appliedScale,
        animationSpec = tween(700, easing = ease),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = appliedAlpha,
        animationSpec = tween(600, easing = ease),
        label = "logoAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // Soft premium background (works with your light theme)
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        MaterialTheme.colorScheme.background
                    ),
                    radius = 1100f
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Logo container (rounded, subtle)
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .offset(y = floatAnim.value.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(26.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = logoRes),
                    contentDescription = "Velora logo",
                    modifier = Modifier.size(92.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // App name (optional — if you want NO word on splash, delete this Text)
            Text(
                text = "Velora",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alpha(logoAlpha)
            )

            Spacer(Modifier.height(8.dp))

            // Slogan
            Text(
                text = slogan,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black.copy(alpha = 0.55f),
                modifier = Modifier.alpha(logoAlpha)
            )
        }
    }
}