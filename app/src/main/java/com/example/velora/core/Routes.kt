package com.example.velora.core

sealed class Route(val value: String){
    data object Splash:Route("splash")
    data object OnBoarding:Route("onboarding")
    data object Login:Route("login")
    data object Register:Route("register")
    data object Home:Route("home")
}