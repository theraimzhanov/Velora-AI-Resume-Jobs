package com.example.velora.presentation.auth


import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AuthBackground(content: @Composable BoxScope.() -> Unit) {
    // Light, soft background like the reference
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4))
    ) {
        // Big circle behind card
        Box(
            Modifier
                .size(460.dp)
                .align(Alignment.Center)
                .offset(y = (-30).dp)
                .background(Color(0xFF2E5E73).copy(alpha = 0.20f), CircleShape)
        )

        // Small decorative dots (top-left & bottom-right)
        Box(
            Modifier
                .size(14.dp)
                .align(Alignment.TopStart)
                .offset(x = 22.dp, y = 90.dp)
                .background(Color(0xFF2E5E73).copy(alpha = 0.45f), CircleShape)
        )
        Box(
            Modifier
                .size(18.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-26).dp, y = (-120).dp)
                .background(Color(0xFF2E5E73).copy(alpha = 0.25f), CircleShape)
        )

        content()
    }
}

@Composable
fun AuthCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    Card(
        modifier = modifier
            .clip(shape)
            .border(1.dp, Color.Black.copy(alpha = 0.06f), shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            content = content
        )
    }
}

@Composable
fun Hero(@DrawableRes resId: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    )
}

@Composable
fun AuthTitle(title: String, subtitle: String) {
    Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(6.dp))
    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Black.copy(alpha = 0.55f))
}

@Composable
fun PillTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    visual: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
) {
    val shape: Shape = RoundedCornerShape(999.dp)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(placeholder, color = Color.Black.copy(alpha = 0.35f)) },
        leadingIcon = leading,
        trailingIcon = trailing,
        visualTransformation = visual,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color(0xFFF2F2F2),
            unfocusedContainerColor = Color(0xFFF2F2F2),
            cursorColor = Color(0xFF2E5E73)
        )
    )
}

@Composable
fun PrimaryAuthButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2E5E73),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF2E5E73).copy(alpha = 0.35f),
            disabledContentColor = Color.White.copy(alpha = 0.9f)
        )
    ) { Text(text, fontWeight = FontWeight.SemiBold) }
}