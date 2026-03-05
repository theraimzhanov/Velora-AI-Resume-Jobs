package com.example.velora.presentation.tracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.velora.domain.auth.AuthState
import com.example.velora.domain.jobs.JobApplication
import com.example.velora.presentation.ui.SoftBackground
import com.example.velora.presentation.ui.SoftCard
import com.example.velora.presentation.ui.SoftChip
import com.example.velora.presentation.ui.SoftIconButton
import com.example.velora.presentation.ui.SoftListItem
import com.example.velora.ui.tokens.Velora
import kotlinx.coroutines.launch

private val StatusFilters = listOf("All", "Applied", "Interview", "Offer", "Rejected")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    authState: AuthState,
    vm: TrackerViewModel = hiltViewModel()
) {
    // Bind user id / start observe
    LaunchedEffect(authState) { vm.bind(authState) }

    val ui by vm.ui.collectAsState()
    val t = Velora.tokens
    val scope = rememberCoroutineScope()

    // Temperature-pill like chips
    var selectedFilter by remember { mutableStateOf("All") }

    // Add bottom sheet
    var sheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Filtered list
    val jobs = remember(ui.jobs, selectedFilter) {
        if (selectedFilter == "All") ui.jobs
        else ui.jobs.filter { it.status == selectedFilter }
    }

    SoftBackground {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                // The screenshot has a + in the main card,
                // but you asked for FAB too: this matches.
                FloatingActionButton(
                    onClick = { sheetOpen = true },
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add application")
                }
            }
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {

                // ===== Header (Good morning + round icons) =====
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Good",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            "morning",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.Black.copy(alpha = 0.35f))
                    }
                }

                SoftIconButton(
                    icon = { Icon(Icons.Rounded.NotificationsNone, contentDescription = "Notifications") },
                    onClick = { /* later */ }
                )

                Spacer(Modifier.width(10.dp))

                SoftIconButton(
                    icon = { Icon(Icons.Rounded.MoreVert, contentDescription = "Menu") },
                    onClick = { /* optional */ }
                )
            }

            Spacer(Modifier.height(14.dp))

            // ===== Big main card (like "Twink Oven") =====
            SoftCard(Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Application Hub",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(6.dp))

                        val subtitle = when {
                            ui.loading -> "Syncing..."
                            ui.jobs.isEmpty() -> "No applications yet"
                            else -> "${ui.jobs.size} total applications"
                        }

                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black.copy(alpha = 0.55f)
                        )
                    }

                    SoftIconButton(
                        icon = { Icon(Icons.Rounded.Add, contentDescription = "Add") },
                        onClick = { sheetOpen = true }
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Two small cards like screenshot "Time still available / heating oven"
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Interviews",
                        value = ui.jobs.count { it.status == "Interview" }.toString()
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Offers",
                        value = ui.jobs.count { it.status == "Offer" }.toString()
                    )
                }

                Spacer(Modifier.height(14.dp))

                // ===== Chips like temperature pills =====
                TemperaturePills(
                    selected = selectedFilter,
                    onSelect = { selectedFilter = it }
                )
            }

            Spacer(Modifier.height(18.dp))

            // ===== List section header (Others Devices) =====
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Other Applications", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = { /* optional */ }) {
                    Text("See all")
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null)
                }
            }

            ui.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(10.dp))

            // ===== List =====
            when {
                ui.loading -> {
                    // Keep simple; if you want shimmer in light theme, tell me
                    repeat(4) {
                        SoftCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            contentPadding = PaddingValues(14.dp)
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(18.dp)
                                    .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(999.dp))
                            )
                            Spacer(Modifier.height(10.dp))
                            Box(
                                Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(14.dp)
                                    .background(Color.Black.copy(alpha = 0.04f), RoundedCornerShape(999.dp))
                            )
                        }
                    }
                }

                jobs.isEmpty() -> {
                    SoftCard(Modifier.fillMaxWidth()) {
                        Text("Nothing here.", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Try another filter or add a new application using +",
                            color = Color.Black.copy(alpha = 0.55f)
                        )
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(jobs, key = { it.id }) { job ->
                            SoftListItem(
                                title = job.company,
                                subtitle = "${job.position} • ${job.status}",
                                leading = { Icon(Icons.Rounded.WorkOutline, contentDescription = null) },
                                trailing = {
                                    // right side small subtle status text
                                    Text(
                                        job.status,
                                        color = if (job.status == "Interview") MaterialTheme.colorScheme.primary
                                        else Color.Black.copy(alpha = 0.45f)
                                    )
                                }
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) } // bottom padding for FAB
                    }
                }
            }
        }
    }

    // ===== Add Application Bottom Sheet =====
    if (sheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { sheetOpen = false },
            sheetState = sheetState
        ) {
            AddApplicationSheet(
                onAdd = { company, position ->
                    vm.add(company, position)
                    scope.launch { sheetState.hide() }
                        .invokeOnCompletion { sheetOpen = false }
                },
                onClose = {
                    scope.launch { sheetState.hide() }
                        .invokeOnCompletion { sheetOpen = false }
                }
            )
            Spacer(Modifier.height(18.dp))
        }
    }
}


@Composable
private fun StatCard(
    modifier: Modifier,
    title: String,
    value: String
) {
    SoftCard(modifier = modifier, contentPadding = PaddingValues(14.dp)) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.Black.copy(alpha = 0.55f))
        Spacer(Modifier.height(10.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
private fun TemperaturePills(
    selected: String,
    onSelect: (String) -> Unit
) {
    // Looks like the screenshot: pill row with one accent selected
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatusFilters.forEach { label ->
            SoftChip(
                text = label,
                selected = label == selected,
                onClick = { onSelect(label) }
            )
        }
    }
}

@Composable
private fun AddApplicationSheet(
    onAdd: (String, String) -> Unit,
    onClose: () -> Unit
) {
    val t = Velora.tokens
    var company by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text("Add Application", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(t.spacing.md))

        OutlinedTextField(
            value = company,
            onValueChange = { company = it },
            label = { Text("Company") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(Modifier.height(t.spacing.sm))

        OutlinedTextField(
            value = position,
            onValueChange = { position = it },
            label = { Text("Position") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(Modifier.height(t.spacing.lg))

        Button(
            onClick = { onAdd(company.trim(), position.trim()) },
            enabled = company.isNotBlank() && position.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Add")
        }

        TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }
}