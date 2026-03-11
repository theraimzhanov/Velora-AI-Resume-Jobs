package com.example.velora.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.velora.ui.tokens.Velora

@Composable
fun SoftBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { content() }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val t = Velora.tokens
    val shape = RoundedCornerShape(t.radius.xl)

    Card(
        modifier = modifier
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.9f), shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Soft UI uses minimal shadow
    ) {
        Column(Modifier.padding(contentPadding), content = content)
    }
}

@Composable
fun SoftChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(999.dp)
    val bg = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFEDEBE6)
    val fg = if (selected) Color.White else MaterialTheme.colorScheme.onSurface

    Surface(
        onClick = onClick,
        shape = shape,
        color = bg,
        tonalElevation = 0.dp,
        modifier = Modifier.height(34.dp)
    ) {
        Box(Modifier.padding(horizontal = 14.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(text, color = fg, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun SoftIconButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFFF1F0EC),
        tonalElevation = 0.dp
    ) {
        Box(Modifier.size(44.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
            icon()
        }
    }
}

@Composable
fun SoftListItem(
    title: String,
    subtitle: String,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leading != null) {
                leading()
                Spacer(Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            if (trailing != null) {
                Spacer(Modifier.width(12.dp))
                trailing()
            }
        }
    }

    SoftCard(modifier = modifier) {
        if (onClick != null) {
            Surface(
                onClick = onClick,
                color = Color.Transparent
            ) {
                content()
            }
        } else {
            content()
        }
    }
}