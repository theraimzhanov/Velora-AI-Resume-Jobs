package com.example.velora.feature.splash

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    hasSeenOnBoarding: Boolean,
    isLoggedIn: Boolean,
    onNavigate:(String)-> Unit
){

    LaunchedEffect(Unit) {
        delay(900)
        when{
            !hasSeenOnBoarding->onNavigate("onboarding")
            !isLoggedIn->onNavigate("login")
            else->onNavigate("home")
        }
    }
    Text("Velora")
}