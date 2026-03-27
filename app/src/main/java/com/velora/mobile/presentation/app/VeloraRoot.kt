package com.velora.mobile.presentation.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.velora.mobile.navigation.VeloraNavHost
import com.velora.mobile.presentation.network.NetworkViewModel
import com.velora.mobile.presentation.network.NoInternetScreen

@Composable
fun VeloraRoot(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    selectedLanguageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    val networkViewModel: NetworkViewModel = hiltViewModel()
    val networkUi by networkViewModel.ui.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (networkUi.checkedAtLeastOnce && !networkUi.isConnected) {
            NoInternetScreen(
                onRetry = { networkViewModel.retryNow() }
            )
        } else {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                VeloraNavHost(
                    modifier = Modifier.padding(innerPadding),
                    darkMode = darkMode,
                    onDarkModeChange = onDarkModeChange,
                    selectedLanguageCode = selectedLanguageCode,
                    onLanguageSelected = onLanguageSelected
                )
            }
        }
    }
}