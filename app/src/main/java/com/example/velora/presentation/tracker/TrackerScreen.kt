package com.example.velora.presentation.tracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

private val Filters = listOf("All") + ApplicationStatus.entries.map { it.label }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    authState: AuthState,
    vm: TrackerViewModel = hiltViewModel()
) {
    LaunchedEffect(authState) { vm.bind(authState) }
    val ui by vm.ui.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }

    val jobs = remember(ui.jobs, selectedFilter) {
        if (selectedFilter == "All") ui.jobs
        else ui.jobs.filter { it.status.equals(selectedFilter, ignoreCase = true) }
    }

    // Bottom sheet
    var sheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    SoftBackground {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButtonPosition = FabPosition.End, // ✅ corner
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { sheetOpen = true },
                    containerColor = Color(0xFF2D63FF),
                    contentColor = Color.White
                ) { Icon(Icons.Rounded.Add, contentDescription = "Add") }
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // ===== Compact hero card =====
                item {
                    CompactHeroCard(
                        total = ui.jobs.size,
                        interviews = ui.jobs.count { it.status == ApplicationStatus.Interview.label },
                        offers = ui.jobs.count { it.status == ApplicationStatus.Offer.label }
                    )
                }

                // ===== Chips (horizontal scroll like pills) =====
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

                // ===== Section title =====
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Applications",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(onClick = { selectedFilter = "All" }) {
                            Text("See all")
                        }
                    }
                }

                // ===== Errors =====
                ui.error?.let { err ->
                    item { Text(err, color = MaterialTheme.colorScheme.error) }
                }

                // ===== Content =====
                when {
                    ui.loading -> {
                        items(4) { TrackerSkeletonRow() }
                    }

                    jobs.isEmpty() -> {
                        item {
                            SoftCard(Modifier.fillMaxWidth()) {
                                Text(
                                    if (selectedFilter == "All") "No applications yet"
                                    else "No $selectedFilter applications",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    if (selectedFilter == "All")
                                        "Tap + to add your first application."
                                    else
                                        "Switch to All to see everything or add a new one.",
                                    color = Color.Black.copy(alpha = 0.55f)
                                )

                                if (selectedFilter != "All") {
                                    Spacer(Modifier.height(12.dp))
                                    Button(
                                        onClick = { selectedFilter = "All" },
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text("Show All") }
                                }
                            }
                        }
                    }

                    else -> {
                        items(jobs, key = { it.id }) { job ->
                            JobRow(job)
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    // ===== Add Sheet =====
    if (sheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { sheetOpen = false },
            sheetState = sheetState
        ) {
            AddApplicationSheet(
                onAdd = { company, position, status ->
                    // ✅ after add, show list (avoid filter hiding new item)
                    selectedFilter = "All"

                    vm.add(company, position, status)
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

            Spacer(Modifier.height(12.dp))

            val progress =
                if (total == 0) 0f else (offers.toFloat() / total.toFloat())

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(100)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Offer rate ${(progress * 100).toInt()}%",
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
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/*@Composable
private fun MiniStatPill(label: String, value: String) {
    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(8.dp))
            Text(label, color = Color.White.copy(alpha = 0.85f))
        }
    }
}*/

@Composable
private fun JobRow(job: JobApplication) {
    val status = ApplicationStatus.fromLabel(job.status)

    SoftListItem(
        title = if (job.company.isBlank()) "(No company)" else job.company,
        subtitle = if (job.position.isBlank()) "—" else job.position,
        leading = { Icon(Icons.Rounded.WorkOutline, contentDescription = null) },
        trailing = { StatusBadge(status) }
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
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(status.label, color = fg, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TrackerSkeletonRow() {
    SoftCard(
        modifier = Modifier.fillMaxWidth(),
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
                .fillMaxWidth(0.65f)
                .height(14.dp)
                .background(Color.Black.copy(alpha = 0.04f), RoundedCornerShape(999.dp))
        )
    }
}