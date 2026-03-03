package com.example.velora.feature.onbarding

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun OnBoardingScreen(
    onFinish:()-> Unit
){
    Button(onClick = onFinish) {
        Text("Finish boarding")
    }
}