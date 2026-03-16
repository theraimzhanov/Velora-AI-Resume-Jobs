package com.example.velora.presentation.app

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.velora.R
import com.example.velora.domain.auth.AuthState
import com.example.velora.navigation.Destinations
import com.example.velora.presentation.profile.ProfileScreen
import com.example.velora.presentation.resume.ResumeScreen
import com.example.velora.presentation.settings.SettingsScreen
import com.example.velora.presentation.tracker.TrackerScreen

private data class Tab(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    authState: AuthState,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    selectedLanguage: String,
    onLanguageSelected: (String, String) -> Unit,
    onLogout: () -> Unit
) {
    val nav = rememberNavController()
    var menuExpanded by remember { mutableStateOf(false) }

    val tabs = listOf(
        Tab(Destinations.TRACKER, "Tracker") {
            Icon(
                painter = painterResource(R.drawable.dast_2),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        },
        Tab(Destinations.RESUME, "Resume") {
            Icon(
                painter = painterResource(R.drawable.dash_1),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    )

    val currentBackStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: Destinations.TRACKER

    val isTopLevelDestination = currentRoute in tabs.map { it.route }
    val showBackButton = !isTopLevelDestination

    val topBarTitle = when (currentRoute) {
        Destinations.TRACKER -> "Velora"
        Destinations.RESUME -> "Resume Checker"
        Destinations.PROFILE -> "Profile"
        Destinations.SETTINGS -> "Settings"
        else -> "Velora"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    if (!showBackButton) {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = "Menu"
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    menuExpanded = false
                                    nav.navigate(Destinations.SETTINGS) {
                                        launchSingleTop = true
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Settings, contentDescription = null)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Log out") },
                                onClick = {
                                    menuExpanded = false
                                    onLogout()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Logout, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (isTopLevelDestination) {
                NavigationBar(
                    tonalElevation = 0.dp,
                    windowInsets = NavigationBarDefaults.windowInsets
                ) {
                    tabs.forEach { tab ->
                        val selected = currentRoute == tab.route

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                nav.navigate(tab.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(nav.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            },
                            icon = { tab.icon() },
                            label = {},
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Destinations.TRACKER,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destinations.TRACKER) {
                TrackerScreen(authState = authState)
            }

            composable(Destinations.RESUME) {
                ResumeScreen()
            }

            composable(Destinations.PROFILE) {
                ProfileScreen(
                    authState = authState,
                    onLogout = onLogout
                )
            }

            composable(Destinations.SETTINGS) {
                SettingsScreen(
                    darkMode = darkMode,
                    onDarkModeChange = onDarkModeChange,
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = onLanguageSelected
                )
            }
        }
    }
}