package com.example.velora.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.example.velora.R
import com.example.velora.domain.auth.AuthState
import com.example.velora.presentation.app.AppScaffold
import com.example.velora.presentation.auth.AuthViewModel
import com.example.velora.presentation.auth.ForgotPasswordScreen
import com.example.velora.presentation.auth.LoginScreen
import com.example.velora.presentation.auth.RegisterScreen
import com.example.velora.presentation.intro.IntroViewModel
import com.example.velora.presentation.intro.OnboardingScreen
import com.example.velora.presentation.intro.SplashScreen

@Composable
fun VeloraNavHost(
    modifier: Modifier = Modifier,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    selectedLanguage: String,
    onLanguageSelected: (String, String) -> Unit
) {
    val nav = rememberNavController()

    val authVm: AuthViewModel = hiltViewModel()
    val authState by authVm.authState.collectAsState()

    val introVm: IntroViewModel = hiltViewModel()
    val introDone by introVm.introDone.collectAsState()

    var splashNavigated by remember { mutableStateOf(false) }

    fun goNextFromSplash() {
        if (splashNavigated) return
        splashNavigated = true

        if (!introDone) {
            nav.navigate(Destinations.ONBOARDING) {
                popUpTo(Destinations.SPLASH) { inclusive = true }
                launchSingleTop = true
            }
            return
        }

        when (authState) {
            AuthState.Loading -> {
                splashNavigated = false
            }

            AuthState.SignedOut -> {
                nav.navigate(Destinations.AUTH_GRAPH) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            is AuthState.SignedIn -> {
                nav.navigate(Destinations.APP_GRAPH) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = nav,
        startDestination = Destinations.INTRO_GRAPH,
        modifier = modifier
    ) {
        navigation(
            startDestination = Destinations.SPLASH,
            route = Destinations.INTRO_GRAPH
        ) {
            composable(Destinations.SPLASH) {
                LaunchedEffect(Unit) {
                    splashNavigated = false
                }

                SplashScreen(
                    logoRes = R.drawable.velora,
                    slogan = "Your career, organized.",
                    onFinish = { goNextFromSplash() }
                )
            }

            composable(Destinations.ONBOARDING) {
                OnboardingScreen(
                    onFinish = {
                        introVm.finishIntro()

                        when (authState) {
                            is AuthState.SignedIn -> {
                                nav.navigate(Destinations.APP_GRAPH) {
                                    popUpTo(Destinations.INTRO_GRAPH) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }

                            AuthState.SignedOut,
                            AuthState.Loading -> {
                                nav.navigate(Destinations.AUTH_GRAPH) {
                                    popUpTo(Destinations.INTRO_GRAPH) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                )
            }
        }

        navigation(
            startDestination = Destinations.LOGIN,
            route = Destinations.AUTH_GRAPH
        ) {
            composable(Destinations.LOGIN) {
                LoginScreen(
                    onGoRegister = { nav.navigate(Destinations.REGISTER) },
                    onGoForgotPassword = { nav.navigate(Destinations.FORGOT_PASSWORD) }
                )
            }

            composable(Destinations.REGISTER) {
                RegisterScreen(
                    onGoLogin = { nav.popBackStack() }
                )
            }

            composable(Destinations.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    onBack = { nav.popBackStack() }
                )
            }
        }

        navigation(
            startDestination = Destinations.APP_SHELL,
            route = Destinations.APP_GRAPH
        ) {
            composable(Destinations.APP_SHELL) {
                AppScaffold(
                    authState = authState,
                    darkMode = darkMode,
                    onDarkModeChange = onDarkModeChange,
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = onLanguageSelected,
                    onLogout = { authVm.logout() }
                )
            }
        }
    }

    LaunchedEffect(authState, introDone) {
        if (!introDone) return@LaunchedEffect

        val currentRoute = nav.currentBackStackEntry?.destination?.route

        when (authState) {
            AuthState.Loading -> Unit

            AuthState.SignedOut -> {
                if (currentRoute != Destinations.LOGIN && currentRoute != Destinations.REGISTER) {
                    nav.navigate(Destinations.AUTH_GRAPH) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            is AuthState.SignedIn -> {
                if (currentRoute != Destinations.APP_SHELL) {
                    nav.navigate(Destinations.APP_GRAPH) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}