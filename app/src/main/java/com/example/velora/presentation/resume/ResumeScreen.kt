package com.example.velora.presentation.resume

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.velora.presentation.ui.SoftBackground
import com.example.velora.presentation.ui.SoftCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeScreen(
    vm: ResumeViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()
    val ctx = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val name = queryName(ctx.contentResolver, uri)
            vm.onFilePicked(uri, name)
        }
    }

    SoftBackground {
        Box(Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                item {
                    SoftCard(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Resume Checker",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Upload a PDF resume and get strengths, weaknesses, ATS fixes.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                )
                            }
                            Icon(Icons.Rounded.Description, contentDescription = null)
                        }

                        Spacer(Modifier.height(14.dp))

                        OutlinedTextField(
                            value = ui.jobTarget,
                            onValueChange = vm::setJobTarget,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Target role (optional)") }
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                picker.launch(arrayOf("application/pdf"))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Icon(Icons.Rounded.UploadFile, contentDescription = null)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                if (ui.fileName == null) "Upload Resume (PDF)"
                                else "Change File"
                            )
                        }

                        ui.fileName?.let {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                "Selected: $it",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                vm.analyze(ctx.contentResolver)
                            },
                            enabled = ui.fileUri != null && !ui.loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(if (ui.loading) "Analyzing…" else "Analyze Resume")
                        }

                        ui.error?.let {
                            Spacer(Modifier.height(10.dp))
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                ui.report?.let { report ->
                    item {
                        SoftCard(Modifier.fillMaxWidth()) {
                            Text("Score", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { (report.overallScore / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("${report.overallScore}/100 • ${report.headline}")
                        }
                    }

                    item { SectionCard("Strengths", report.strengths) }
                    item { SectionCard("Weaknesses", report.weaknesses) }
                    item { SectionCard("Quick Fixes", report.quickFixes) }
                    item { SectionCard("Keyword Gaps", report.keywordGaps) }
                    item { SectionCard("ATS Notes", report.atsNotes) }

                    item {
                        SoftCard(Modifier.fillMaxWidth()) {
                            Text("Suggested Summary", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text(report.suggestedSummary)
                        }
                    }

                    item { Spacer(Modifier.height(90.dp)) }
                }
            }

            AnimatedVisibility(visible = ui.loading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text(ui.progressLabel ?: "Working…")
                        Spacer(Modifier.height(10.dp))
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, items: List<String>) {
    SoftCard(Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        items.take(8).forEach {
            Text("• $it", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(6.dp))
        }
    }
}

private fun queryName(cr: android.content.ContentResolver, uri: android.net.Uri): String? {
    val c = cr.query(uri, null, null, null, null) ?: return null
    return c.use {
        val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst() && idx >= 0) it.getString(idx) else null
    }
}