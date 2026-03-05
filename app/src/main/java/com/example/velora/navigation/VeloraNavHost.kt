package com.example.velora.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.example.velora.R
import com.example.velora.domain.auth.AuthState
import com.example.velora.presentation.app.AppScaffold
import com.example.velora.presentation.auth.AuthViewModel
import com.example.velora.presentation.auth.LoginScreen
import com.example.velora.presentation.auth.RegisterScreen
import com.example.velora.presentation.intro.IntroViewModel
import com.example.velora.presentation.intro.OnboardingScreen
import com.example.velora.presentation.intro.SplashScreen

@Composable
fun VeloraNavHost() {
    val nav = rememberNavController()

    val authVm: AuthViewModel = hiltViewModel()
    val authState by authVm.authState.collectAsState()

    val introVm: IntroViewModel = hiltViewModel()
    val introDone by introVm.introDone.collectAsState()

    // Guard to prevent double navigation (common bug in Compose)
    var splashNavigated by remember { mutableStateOf(false) }

    fun goNextFromSplash() {
        if (splashNavigated) return
        splashNavigated = true

        if (!introDone) {
            nav.navigate(Destinations.ONBOARDING) {
                popUpTo(Destinations.SPLASH) { inclusive = true }
            }
            return
        }

        when (authState) {
            AuthState.Loading -> {
                // If still loading, just go to auth graph (safe default).
                nav.navigate(Destinations.AUTH_GRAPH) {
                    popUpTo(0) { inclusive = true }
                }
            }
            AuthState.SignedOut -> {
                nav.navigate(Destinations.AUTH_GRAPH) {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.SignedIn -> {
                nav.navigate(Destinations.APP_GRAPH) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(navController = nav, startDestination = Destinations.INTRO_GRAPH) {

        // INTRO
        navigation(startDestination = Destinations.SPLASH, route = Destinations.INTRO_GRAPH) {

            composable(Destinations.SPLASH) {
                // reset guard if user ever returns here (rare, but safe)
                LaunchedEffect(Unit) { splashNavigated = false }

                SplashScreen(
                    logoRes = R.drawable.velora, // <-- put your logo into drawable as velora_mark.png
                    slogan = "Your career, organized.",
                    onFinish = { goNextFromSplash() }
                )
            }

            composable(Destinations.ONBOARDING) {
                OnboardingScreen(
                    onFinish = {
                        introVm.finishIntro()
                        // after onboarding, decide based on auth
                        when (authState) {
                            AuthState.Loading -> nav.navigate(Destinations.AUTH_GRAPH) { popUpTo(0) { inclusive = true } }
                            AuthState.SignedOut -> nav.navigate(Destinations.AUTH_GRAPH) { popUpTo(0) { inclusive = true } }
                            is AuthState.SignedIn -> nav.navigate(Destinations.APP_GRAPH) { popUpTo(0) { inclusive = true } }
                        }
                    }
                )
            }
        }

        // AUTH
        navigation(startDestination = Destinations.LOGIN, route = Destinations.AUTH_GRAPH) {
            composable(Destinations.LOGIN) {
                LoginScreen(onGoRegister = { nav.navigate(Destinations.REGISTER) })
            }
            composable(Destinations.REGISTER) {
                RegisterScreen(onGoLogin = { nav.popBackStack() })
            }
        }

        // APP
        navigation(startDestination = Destinations.APP_SHELL, route = Destinations.APP_GRAPH) {
            composable(Destinations.APP_SHELL) {
                AppScaffold(
                    authState = authState,
                    onLogout = { authVm.logout() }
                )
            }
        }
    }

    /**
     * Keep auth/app graphs synced AFTER intro is done.
     * Important: if intro is not done, do nothing (user should stay in intro/onboarding flow).
     */
    LaunchedEffect(introDone, authState) {
        if (!introDone) return@LaunchedEffect

        when (authState) {
            AuthState.Loading -> Unit
            AuthState.SignedOut -> nav.navigate(Destinations.AUTH_GRAPH) {
                popUpTo(0) { inclusive = true }
            }
            is AuthState.SignedIn -> nav.navigate(Destinations.APP_GRAPH) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}