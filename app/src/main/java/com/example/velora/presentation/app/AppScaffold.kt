package com.example.velora.presentation.app

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.velora.R
import com.example.velora.domain.auth.AuthState
import com.example.velora.navigation.Destinations
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
    selectedLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
    onLogout: () -> Unit
) {
    val nav = rememberNavController()
    var menuExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

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
        Destinations.RESUME -> stringResource(R.string.resume_checker)
        Destinations.SETTINGS -> stringResource(R.string.settings)
        else -> "Velora"
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = stringResource(R.string.log_out))
            },
            text = {
                Text(text = stringResource(R.string.are_you_sure_you_want_to_log_out))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text(stringResource(R.string.no))
                }
            }
        )
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
                                contentDescription = null
                            )
                        }
                    }
                },
                actions = {
                    if (!showBackButton) {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = null
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.settings)) },
                                onClick = {
                                    menuExpanded = false
                                    nav.navigate(Destinations.SETTINGS) {
                                        launchSingleTop = true
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = null
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.log_out)) },
                                onClick = {
                                    menuExpanded = false
                                    showLogoutDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Logout,
                                        contentDescription = null
                                    )
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
            composable(Destinations.SETTINGS) {
                SettingsScreen(
                    darkMode = darkMode,
                    onDarkModeChange = onDarkModeChange,
                    selectedLanguageCode = selectedLanguageCode,
                    onLanguageSelected = onLanguageSelected
                )
            }
        }
    }
}