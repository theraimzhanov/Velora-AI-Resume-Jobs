package com.example.velora.presentation.resume


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.velora.presentation.ui.*

@Composable
fun ResumeScreen(vm: ResumeViewModel = hiltViewModel()) {
    val ui by vm.ui.collectAsState()

    VeloraBackground {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Resume Checker", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(10.dp))

            VeloraCard(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = ui.input,
                    onValueChange = vm::setInput,
                    label = { Text("Paste resume text") },
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )

                Spacer(Modifier.height(12.dp))
                VeloraButton(
                    text = if (ui.loading) "Analyzing..." else "Analyze",
                    enabled = !ui.loading && ui.input.isNotBlank()
                ) { vm.analyze() }

                ui.error?.let { Spacer(Modifier.height(10.dp)); Text(it, color = MaterialTheme.colorScheme.error) }
            }

            ui.result?.let { r ->
                Spacer(Modifier.height(14.dp))
                VeloraCard(Modifier.fillMaxWidth()) {
                    Text("Score: ${r.score}/100", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(10.dp))
                    Section("Strengths", r.strengths)
                    Spacer(Modifier.height(10.dp))
                    Section("Issues", r.issues)
                    Spacer(Modifier.height(10.dp))
                    Section("Suggestions", r.suggestions)
                }
            }
        }
    }
}

@Composable
private fun Section(title: String, items: List<String>) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(6.dp))
    items.take(6).forEach { Text("• $it") }
}