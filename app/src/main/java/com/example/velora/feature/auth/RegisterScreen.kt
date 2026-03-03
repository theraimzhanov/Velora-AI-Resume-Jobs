package com.example.velora.feature.auth

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoLogin: () -> Unit
){
    Button(onClick = onRegisterSuccess) { Text("Create account") }
    Button(onClick = onGoLogin) { Text("Back to Login") }
}