package com.example.velora.presentation.tracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.velora.domain.auth.AuthState
import com.example.velora.domain.jobs.ApplicationStatus
import com.example.velora.domain.jobs.JobApplication
import com.example.velora.presentation.ui.SoftBackground
import com.example.velora.presentation.ui.SoftCard
import com.example.velora.presentation.ui.SoftChip
import com.example.velora.presentation.ui.SoftListItem
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip

private val Filters = listOf("All") + ApplicationStatus.entries.map { it.label }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    authState: AuthState,
    vm: TrackerViewModel = hiltViewModel()
) {
    LaunchedEffect(authState) {
        vm.bind(authState)
    }

    val ui by vm.ui.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    var addSheetOpen by remember { mutableStateOf(false) }
    var selectedJob by remember { mutableStateOf<JobApplication?>(null) }

    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val actionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val jobs = remember(ui.jobs, selectedFilter) {
        if (selectedFilter == "All") {
            ui.jobs
        } else {
            ui.jobs.filter { it.status.equals(selectedFilter, ignoreCase = true) }
        }
    }

    SoftBackground {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { addSheetOpen = true },
                    containerColor = Color(0xFF2D63FF),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add application"
                    )
                }
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    CompactHeroCard(
                        total = ui.jobs.size,
                        interviews = ui.jobs.count {
                            it.status.equals(ApplicationStatus.Interview.label, ignoreCase = true)
                        },
                        offers = ui.jobs.count {
                            it.status.equals(ApplicationStatus.Offer.label, ignoreCase = true)
                        }
                    )
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(Filters) { label ->
                            SoftChip(
                                text = label,
                                selected = label == selectedFilter,
                                onClick = { selectedFilter = label }
                            )
                        }
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Applications",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(onClick = { selectedFilter = "All" }) {
                            Text("See all")
                        }
                    }
                }

                ui.error?.let { err ->
                    item {
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                when {
                    ui.loading -> {
                        items(4) {
                            TrackerSkeletonRow()
                        }
                    }

                    jobs.isEmpty() -> {
                        item {
                            SoftCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (selectedFilter == "All") {
                                        "No applications yet"
                                    } else {
                                        "No $selectedFilter applications"
                                    },
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (selectedFilter == "All") {
                                        "Tap + to add your first application."
                                    } else {
                                        "Switch to All to see everything or add a new one."
                                    },
                                    color = Color.Black.copy(alpha = 0.55f)
                                )

                                if (selectedFilter != "All") {
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = { selectedFilter = "All" },
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Show All")
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        items(
                            items = jobs,
                            key = { it.id }
                        ) { job ->
                            JobRow(
                                job = job,
                                onClick = {
                                    selectedJob = job
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }

    if (addSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { addSheetOpen = false },
            sheetState = addSheetState
        ) {
            AddApplicationSheet(
                onAdd = { company, position, status ->
                    selectedFilter = "All"
                    vm.add(company, position, status)

                    scope.launch {
                        addSheetState.hide()
                    }.invokeOnCompletion {
                        addSheetOpen = false
                    }
                },
                onClose = {
                    scope.launch {
                        addSheetState.hide()
                    }.invokeOnCompletion {
                        addSheetOpen = false
                    }
                }
            )

            Spacer(modifier = Modifier.height(18.dp))
        }
    }

    selectedJob?.let { job ->
        ModalBottomSheet(
            onDismissRequest = { selectedJob = null },
            sheetState = actionSheetState
        ) {
            ApplicationActionsSheet(
                job = job,
                onStatusChange = { newStatus ->
                    vm.setStatus(job.id, newStatus)

                    scope.launch {
                        actionSheetState.hide()
                    }.invokeOnCompletion {
                        selectedJob = null
                    }
                },
                onDelete = {
                    vm.delete(job.id)

                    scope.launch {
                        actionSheetState.hide()
                    }.invokeOnCompletion {
                        selectedJob = null
                    }
                },
                onClose = {
                    scope.launch {
                        actionSheetState.hide()
                    }.invokeOnCompletion {
                        selectedJob = null
                    }
                }
            )

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun CompactHeroCard(
    total: Int,
    interviews: Int,
    offers: Int
) {
    SoftCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBlock(
                    title = "Total",
                    value = total.toString(),
                    modifier = Modifier.weight(1f)
                )

                StatBlock(
                    title = "Interviews",
                    value = interviews.toString(),
                    modifier = Modifier.weight(1f)
                )

                StatBlock(
                    title = "Offers",
                    value = offers.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val progress =
                if (total == 0) 0f else offers.toFloat() / total.toFloat()

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(100)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Offer rate ${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun StatBlock(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun JobRow(
    job: JobApplication,
    onClick: () -> Unit
) {
    val status = ApplicationStatus.fromLabel(job.status)

    SoftListItem(
        title = if (job.company.isBlank()) "(No company)" else job.company,
        subtitle = buildString {
            append(if (job.position.isBlank()) "—" else job.position)
            append(" • ")
            append(formatApplicationDate(job.createdAt))
        },
        leading = {
            Icon(
                imageVector = Icons.Rounded.WorkOutline,
                contentDescription = null
            )
        },
        trailing = {
            StatusBadge(status = status)
        },
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    )
}

@Composable
private fun StatusBadge(status: ApplicationStatus) {
    val (bg, fg) = when (status) {
        ApplicationStatus.Applied -> Color(0xFFE9EEF8) to Color(0xFF2D63FF)
        ApplicationStatus.Interview -> Color(0xFFFFF1E6) to Color(0xFFCC6A19)
        ApplicationStatus.Offer -> Color(0xFFE7F7EF) to Color(0xFF1F9D5A)
        ApplicationStatus.Rejected -> Color(0xFFFFE8EA) to Color(0xFFCC2F3A)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = status.label,
            color = fg,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TrackerSkeletonRow() {
    SoftCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(
                    Color.Black.copy(alpha = 0.05f),
                    RoundedCornerShape(999.dp)
                )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(14.dp)
                .background(
                    Color.Black.copy(alpha = 0.04f),
                    RoundedCornerShape(999.dp)
                )
        )
    }
}