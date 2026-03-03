package com.example.velora.core.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class VeloraAppState{
    var hasSeenOnBoarding by mutableStateOf(false)
    var isLoggedIn by mutableStateOf(false)
}