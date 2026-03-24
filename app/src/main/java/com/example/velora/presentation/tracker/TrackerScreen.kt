package com.example.velora.presentation.tracker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Business
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.velora.R
import com.example.velora.domain.auth.AuthState
import com.example.velora.domain.jobs.ApplicationStatus
import com.example.velora.domain.jobs.JobApplication
import com.example.velora.presentation.ui.SoftBackground
import com.example.velora.presentation.ui.SoftCard
import kotlinx.coroutines.launch

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

    var addSheetOpen by remember { mutableStateOf(false) }
    var selectedJob by remember { mutableStateOf<JobApplication?>(null) }

    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val actionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val total = ui.jobs.size
    val interviews = ui.jobs.count {
        it.status.equals(ApplicationStatus.Interview.name, ignoreCase = true)
    }
    val offers = ui.jobs.count {
        it.status.equals(ApplicationStatus.Offer.name, ignoreCase = true)
    }

    val interviewRate = if (total == 0) 0 else ((interviews.toFloat() / total.toFloat()) * 100f).toInt()
    val offerRate = if (total == 0) 0 else ((offers.toFloat() / total.toFloat()) * 100f).toInt()

    SoftBackground {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { addSheetOpen = true },
                    containerColor = Color(0xFFBFDDFB),
                    contentColor = Color(0xFF3277D8)
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
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 18.dp,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
             /*   item {
                    Text(
                        text = "Track Your\nApplications",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF171A22)
                    )
                }*/

                item {
                    TrackerHeroCard(
                        total = total,
                        interviews = interviews,
                        offers = offers
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatMiniCard(
                            title = stringResource(R.string.applications),
                            value = total.toString(),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            containerColor = Color(0xFFF7F1EA)
                        )

                        StatMiniCard(
                            title = stringResource(R.string.interview_rate),
                            value = "$interviewRate%",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            containerColor = Color(0xFFF2F4FB)
                        )

                        StatMiniCard(
                            title = stringResource(R.string.offer_rate),
                            value = "$offerRate%",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            containerColor = Color(0xFFF2F4FB)
                        )
                    }
                }

                item {
                    PremiumSectionHeader(
                        title = stringResource(R.string.saved),
                        subtitle = "(${ui.jobs.size})"
                    )
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
                            PremiumTrackerSkeleton()
                        }
                    }

                    ui.jobs.isEmpty() -> {
                        item {
                            EmptyTrackerCard(
                                onAddClick = { addSheetOpen = true }
                            )
                        }
                    }

                    else -> {
                        items(
                            items = ui.jobs.sortedByDescending { it.createdAt },
                            key = { it.id }
                        ) { job ->
                            PremiumJobCard(
                                job = job,
                                onClick = { selectedJob = job }
                            )
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
private fun TrackerHeroCard(
    total: Int,
    interviews: Int,
    offers: Int
) {
    val goal = 20
    val progress = (total.toFloat() / goal.toFloat()).coerceIn(0f, 1f)

    SoftCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(18.dp),
        containerColor = Color(0xFFF6F8FD)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.your_job_search),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF171A22)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when {
                    total == 0 -> stringResource(R.string.tr_1)
                    offers > 0 -> stringResource(R.string.tr_2)
                    interviews > 0 -> stringResource(R.string.tr_3)
                    else -> stringResource(R.string.tr_4)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF667085)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.goal_progress),
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF344054),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = Color(0xFF5C96F5),
                trackColor = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.of_applications_target, total, goal),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF98A2B3)
            )
        }
    }
}


@Composable
private fun StatMiniCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color
) {
    SoftCard(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 10.dp),
        containerColor = containerColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF171A22),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF667085),
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
@Composable
private fun PremiumSectionHeader(
    title: String,
    subtitle: String
) {
    SoftCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        containerColor = Color(0xFFF7F7FB)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE9F2FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Bookmark,
                    contentDescription = null,
                    tint = Color(0xFF5C96F5),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF171A22)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF98A2B3)
            )
        }
    }
}
@Composable
private fun PremiumJobCard(
    job: JobApplication,
    onClick: () -> Unit
) {
    val status = ApplicationStatus.fromName(job.status)
    val companyAccent = companyAccent(job.company)

    SoftCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            CompanyBadge(
                company = job.company,
                accent = companyAccent
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (job.company.isBlank()) stringResource(R.string.no_company) else job.company,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    PremiumStatusPill(status = status)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (job.position.isBlank()) stringResource(R.string.unknown_position) else job.position,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF344054),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatApplicationDate(job.createdAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF98A2B3)
                )

                Spacer(modifier = Modifier.height(10.dp))

                PremiumProgressRow(status = status)
            }
        }
    }
}

@Composable
private fun CompanyBadge(
    company: String,
    accent: Color
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(accent.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
    ) {
        val first = company.trim().firstOrNull()?.uppercase() ?: "•"

        Text(
            text = first,
            color = accent,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PremiumStatusPill(status: ApplicationStatus) {
    val (bg, fg) = when (status) {
        ApplicationStatus.Applied -> Color(0xFFF5ECD9) to Color(0xFF9A7A27)
        ApplicationStatus.Interview -> Color(0xFFE8F0FF) to Color(0xFF407BFF)
        ApplicationStatus.Offer -> Color(0xFFE8F8EE) to Color(0xFF25935C)
        ApplicationStatus.Rejected -> Color(0xFFFFECEE) to Color(0xFFC43D4C)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = status.name,
            color = fg,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PremiumProgressRow(status: ApplicationStatus) {
    val progress = when (status) {
        ApplicationStatus.Applied -> 0.30f
        ApplicationStatus.Interview -> 0.65f
        ApplicationStatus.Offer -> 1f
        ApplicationStatus.Rejected -> 1f
    }

    val color = when (status) {
        ApplicationStatus.Applied -> Color(0xFFB6C8E8)
        ApplicationStatus.Interview -> Color(0xFF7FAEFF)
        ApplicationStatus.Offer -> Color(0xFF53C487)
        ApplicationStatus.Rejected -> Color(0xFFE09AA4)
    }

    Column {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = color,
            trackColor = Color(0xFFF0F2F6)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = when (status) {
                ApplicationStatus.Applied -> stringResource(R.string.application_submitted)
                ApplicationStatus.Interview -> stringResource(R.string.interview_pipeline_in_progress)
                ApplicationStatus.Offer -> stringResource(R.string.offer_received)
                ApplicationStatus.Rejected -> stringResource(R.string.process_closed)
            },
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF98A2B3)
        )
    }
}

@Composable
private fun EmptyTrackerCard(
    onAddClick: () -> Unit
) {
    SoftCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(18.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAF2FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Business,
                    contentDescription = null,
                    tint = Color(0xFF5C96F5)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.no_applications_yet),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.tracker),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF667085)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBFDDFB),
                    contentColor = Color(0xFF3277D8)
                )
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.add_application))
            }
        }
    }
}

@Composable
private fun PremiumTrackerSkeleton() {
    SoftCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black.copy(alpha = 0.05f))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.Black.copy(alpha = 0.05f))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.42f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.Black.copy(alpha = 0.04f))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.Black.copy(alpha = 0.04f))
                )
            }
        }
    }
}

private fun companyAccent(company: String): Color {
    val key = company.trim().lowercase()

    return when {
        "google" in key -> Color(0xFF4285F4)
        "meta" in key -> Color(0xFF1877F2)
        "netflix" in key -> Color(0xFFE50914)
        "stripe" in key -> Color(0xFF635BFF)
        else -> Color(0xFF5C96F5)
    }
}