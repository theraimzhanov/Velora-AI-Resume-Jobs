package com.example.velora.core.navigation

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.velora.core.Route
import com.example.velora.feature.auth.LoginScreen
import com.example.velora.feature.auth.RegisterScreen
import com.example.velora.feature.home.HomeScreen
import com.example.velora.feature.onbarding.OnBoardingScreen
import com.example.velora.feature.splash.SplashScreen

@Composable
fun VeloraNavHost(
    navController: NavHostController = rememberNavController(),
    appState: VeloraAppState = remember{ VeloraAppState() }
){

    NavHost(navController=navController,
        startDestination = Route.Splash.value){
composable(Route.Splash.value){
    SplashScreen(
        hasSeenOnBoarding = appState.hasSeenOnBoarding,
        isLoggedIn = appState.isLoggedIn,
        onNavigate = {route->
            navController.navigate(route){
                popUpTo(Route.Splash.value){
                    inclusive = true
                }
            }
        })
            }

        composable(Route.OnBoarding.value) {
            OnBoardingScreen(
                onFinish = {
                    appState.hasSeenOnBoarding = true
                    navController.navigate(Route.Login.value) {
                        popUpTo(Route.OnBoarding.value) { inclusive = true }
                    }
                }
            )
        }


        composable(Route.Login.value) {
            LoginScreen(
                onLoginSuccess = {
                    appState.isLoggedIn = true
                    navController.navigate(Route.Home.value) {
                        popUpTo(Route.Login.value) { inclusive = true }
                    }
                },
                onGoRegister = { navController.navigate(Route.Register.value) }
            )
        }

        composable(Route.Register.value) {
            RegisterScreen(
                onRegisterSuccess = {
                    appState.isLoggedIn = true
                    navController.navigate(Route.Home.value) {
                        popUpTo(Route.Register.value) { inclusive = true }
                    }
                },
                onGoLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Route.Home.value) {
            HomeScreen()
        }
        }
}
