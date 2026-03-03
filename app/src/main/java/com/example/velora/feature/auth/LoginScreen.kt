package com.example.velora.feature.auth

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun LoginScreen(
    onLoginSuccess:()->Unit,
    onGoRegister:()-> Unit
){
    Button(onClick = onLoginSuccess){
        Text("Login")
        }
    Button(onClick = onGoRegister){
        Text("Go to Register")
    }
}