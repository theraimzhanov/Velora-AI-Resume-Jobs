package com.example.velora.presentation.profile


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.velora.domain.auth.AuthState
import com.example.velora.presentation.ui.*

@Composable
fun ProfileScreen(authState: AuthState, onLogout: () -> Unit) {
    val email = (authState as? AuthState.SignedIn)?.email ?: "Unknown"

    VeloraBackground {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Profile", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))

            VeloraCard(Modifier.fillMaxWidth()) {
                Text("Account", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(6.dp))
                Text(email)
                Spacer(Modifier.height(12.dp))
                VeloraButton("Log out", enabled = true, onClick = onLogout)
            }
        }
    }
}