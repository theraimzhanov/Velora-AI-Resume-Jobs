package com.example.velora.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.velora.presentation.ui.VeloraBackground
import com.example.velora.presentation.ui.VeloraCard

@Composable
fun SettingsScreen() {
    var notifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(true) }

    VeloraBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            VeloraCard(Modifier.fillMaxWidth()) {
                Toggle("Notifications", notifications) { notifications = it }
                Spacer(Modifier.height(10.dp))
                Toggle("Dark Mode (stub)", darkMode) { darkMode = it }
                Spacer(Modifier.height(10.dp))
                Text("Next: DataStore + real theme settings.")
            }
        }
    }
}

@Composable
private fun Toggle(
    title: String,
    value: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = value,
            onCheckedChange = onChange
        )
    }
}