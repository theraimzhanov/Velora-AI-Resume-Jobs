package com.example.velora.presentation.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.velora.domain.auth.AuthState
import com.example.velora.navigation.Destinations
import com.example.velora.presentation.profile.ProfileScreen
import com.example.velora.presentation.resume.ResumeScreen
import com.example.velora.presentation.settings.SettingsScreen
import com.example.velora.presentation.tracker.TrackerScreen

private data class Tab(val route: String, val label: String, val icon: @Composable () -> Unit)

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    authState: AuthState,
    onLogout: () -> Unit
) {
    val nav = rememberNavController()

    val tabs = listOf(
        Tab(Destinations.TRACKER, "Tracker") { Icon(Icons.Rounded.Work, null) },
        Tab(Destinations.RESUME, "Resume") { Icon(Icons.Rounded.Description, null) },
        Tab(Destinations.PROFILE, "Profile") { Icon(Icons.Rounded.AccountCircle, null) },
    )

    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route ?: Destinations.TRACKER
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Velora") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                menuExpanded = false
                                nav.navigate(Destinations.SETTINGS) { launchSingleTop = true }
                            },
                            leadingIcon = { Icon(Icons.Rounded.Settings, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Log out") },
                            onClick = {
                                menuExpanded = false
                                onLogout()
                            },
                            leadingIcon = { Icon(Icons.Rounded.Logout, null) }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEach { t ->
                    NavigationBarItem(
                        selected = currentRoute == t.route,
                        onClick = { nav.navigate(t.route) { launchSingleTop = true } },
                        icon = t.icon,
                        label = { Text(t.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = Destinations.TRACKER, modifier = Modifier.padding(padding)) {
            composable(Destinations.TRACKER) { TrackerScreen(authState = authState) }
            composable(Destinations.RESUME) { ResumeScreen() }
            composable(Destinations.PROFILE) { ProfileScreen(authState = authState, onLogout = onLogout) }
            composable(Destinations.SETTINGS) { SettingsScreen() }
        }
    }
}