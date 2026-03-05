package com.example.velora.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.velora.ui.tokens.Velora

@Composable
fun VeloraBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        MaterialTheme.colorScheme.background
                    ),
                    radius = 1200f
                )
            )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    )
                )
        )
        content()
    }
}

@Composable
fun VeloraCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val t = Velora.tokens
    val shape = RoundedCornerShape(t.radius.xl)

    Card(
        modifier = modifier
            .clip(shape)
            .border(1.dp, Color.White.copy(alpha = 0.06f), shape),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.80f)),
        elevation = CardDefaults.cardElevation(defaultElevation = t.elevation.card),
        shape = shape
    ) {
        Column(Modifier.padding(t.spacing.lg), content = content)
    }
}

@Composable
fun VeloraTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    password: Boolean = false,
    modifier: Modifier = Modifier
) {
    val t = Velora.tokens
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(t.radius.lg),
        visualTransformation = if (password)
            androidx.compose.ui.text.input.PasswordVisualTransformation()
        else androidx.compose.ui.text.input.VisualTransformation.None
    )
}

@Composable
fun VeloraButton(
    text: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(text)
    }
}