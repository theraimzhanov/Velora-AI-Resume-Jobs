package com.example.velora.presentation.resume

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.TrackChanges
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.velora.R
import com.example.velora.domain.resume.ResumeReport
import com.example.velora.domain.resume.RoadmapStep
import com.example.velora.presentation.ui.SoftBackground
import com.example.velora.presentation.ui.SoftCard
import com.example.velora.presentation.ui.SoftChip

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
            val mime = ctx.contentResolver.getType(uri)
            vm.onFilePicked(uri, name, mime)
        }
    }

    SoftBackground {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ResumeHeroInputCard(
                    fileName = ui.fileName,
                    jobTarget = ui.jobTarget,
                    analysisLanguageInput = ui.analysisLanguageInput,
                    appLanguageName = ui.appLanguageName,
                    onJobTargetChange = vm::setJobTarget,
                    onLanguageChange = vm::setAnalysisLanguageInput,
                    onPickFile = {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        picker.launch(
                            arrayOf(
                                "application/pdf",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                            )
                        )
                    },
                    onAnalyze = {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        vm.analyze(ctx.contentResolver)
                    },
                    analyzeEnabled = ui.fileUri != null && !ui.loading,
                    loading = ui.loading,
                    error = ui.error
                )

                ui.report?.let { report ->
                    ScoreOverviewCard(report)
                    CompactReviewCard(report)

                    if (report.recruiterConcerns.isNotEmpty()) {
                        InsightListCard(
                            title = stringResource(R.string.why_recruiter_may_skip),
                            subtitle = stringResource(R.string.main_shortlist_risks),
                            items = report.recruiterConcerns,
                            icon = Icons.Rounded.RecordVoiceOver
                        )
                    }

                    if (report.redFlags.isNotEmpty()) {
                        InsightListCard(
                            title = stringResource(R.string.red_flags),
                            subtitle = stringResource(R.string.problems_to_fix_first),
                            items = report.redFlags,
                            icon = Icons.Rounded.Flag
                        )
                    }

                    SkillsGapCard(
                        detectedSkills = report.detectedSkills,
                        missingSkills = report.missingSkills,
                        keywordGaps = report.keywordGaps
                    )

                    if (report.quickFixes.isNotEmpty()) {
                        InsightListCard(
                            title = stringResource(R.string.quick_fixes),
                            subtitle = stringResource(R.string.fast_improvements),
                            items = report.quickFixes,
                            icon = Icons.Rounded.Lightbulb
                        )
                    }

                    if (report.strengths.isNotEmpty()) {
                        InsightListCard(
                            title = stringResource(R.string.strengths),
                            subtitle = stringResource(R.string.what_already_helps_you),
                            items = report.strengths,
                            icon = Icons.Rounded.CheckCircle
                        )
                    }

                    if (report.atsNotes.isNotEmpty()) {
                        InsightListCard(
                            title = stringResource(R.string.ats_notes),
                            subtitle = stringResource(R.string.scanner_and_keyword_review),
                            items = report.atsNotes,
                            icon = Icons.Rounded.TrackChanges
                        )
                    }

                    if (report.roadmap.isNotEmpty()) {
                        RoadmapCard(report.roadmap)
                    }

                    if (report.suggestedSummary.isNotBlank()) {
                        SuggestedSummaryCard(report.suggestedSummary)
                    }

                    Spacer(Modifier.height(80.dp))
                }
            }

            AnimatedVisibility(visible = ui.loading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = ui.progressLabel ?: "Analyzing…",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(10.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumeHeroInputCard(
    fileName: String?,
    jobTarget: String,
    analysisLanguageInput: String,
    appLanguageName: String,
    onJobTargetChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onPickFile: () -> Unit,
    onAnalyze: () -> Unit,
    analyzeEnabled: Boolean,
    loading: Boolean,
    error: String?
) {
    SoftCard(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Resume / CV Audit",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.uploadd),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.70f)
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = jobTarget,
            onValueChange = onJobTargetChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.target_role_optional)) },
            placeholder = { Text(stringResource(R.string.example_android_developer_intern)) }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = analysisLanguageInput,
            onValueChange = onLanguageChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.answer_language_optional)) },
            placeholder = { Text("Default: $appLanguageName") },
            leadingIcon = {
                Icon(Icons.Rounded.Language, contentDescription = null)
            }
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.leave_empty_to_use_app_language, appLanguageName),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )

        Spacer(Modifier.height(14.dp))

        FileStatusCard(fileName = fileName, onPickFile = onPickFile)

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onAnalyze,
            enabled = analyzeEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Icon(Icons.Rounded.Psychology, contentDescription = null)
            Spacer(Modifier.width(10.dp))
            Text(if (loading) stringResource(R.string.analyzing) else stringResource(R.string.run_audit))
        }

        error?.let {
            Spacer(Modifier.height(10.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun FileStatusCard(
    fileName: String?,
    onPickFile: () -> Unit
) {
    SoftCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp),
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (fileName == null) Icons.Rounded.UploadFile else Icons.Rounded.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = fileName ?: stringResource(R.string.no_resume_cv_selected),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Supported: PDF, DOCX",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(onClick = onPickFile) {
                Text(if (fileName == null) stringResource(R.string.upload) else stringResource(R.string.change))
            }
        }
    }
}

@Composable
private fun ScoreOverviewCard(report: ResumeReport) {
    SoftCard(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreRing(
                score = report.overallScore,
                label = stringResource(R.string.quality),
                modifier = Modifier.size(88.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.resume_score),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = report.headline,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                )
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { (report.overallScore / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(999.dp))
                )
            }
        }
    }
}

@Composable
private fun CompactReviewCard(report: ResumeReport) {
    SoftCard(Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.recruiter_verdict),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = report.recruiterVerdict,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            ScoreRing(
                score = report.targetFitScore,
                label = stringResource(R.string.fit),
                modifier = Modifier.size(78.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                BulletItem(stringResource(R.string.role_fit, report.targetRoleAssessment))
                Spacer(Modifier.height(6.dp))
                BulletItem(stringResource(R.string.format, report.formatAssessment))
                Spacer(Modifier.height(6.dp))
                BulletItem(stringResource(R.string.length, report.lengthAssessment))
            }
        }
    }
}

@Composable
private fun ScoreRing(
    score: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { (score / 100f).coerceIn(0f, 1f) },
            strokeWidth = 8.dp,
            modifier = Modifier.fillMaxSize()
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)
            )
        }
    }
}

@Composable
private fun InsightListCard(
    title: String,
    subtitle: String,
    items: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    SoftCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        items.take(6).forEach { item ->
            BulletItem(text = item)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SkillsGapCard(
    detectedSkills: List<String>,
    missingSkills: List<String>,
    keywordGaps: List<String>
) {
    SoftCard(Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.skills_gap),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.current_skills_vs_missing_ones),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )

        Spacer(Modifier.height(16.dp))

        Text("Detected", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        ChipWrap(
            items = detectedSkills.ifEmpty { listOf(stringResource(R.string.no_strong_skills_detected)) },
            selected = true
        )

        Spacer(Modifier.height(18.dp))

        Text("Missing / weak", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        ChipWrap(
            items = missingSkills.ifEmpty { keywordGaps.ifEmpty { listOf(stringResource(R.string.no_major_gaps_detected)) } },
            selected = false
        )
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipWrap(
    items: List<String>,
    selected: Boolean
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.take(12).forEach { skill ->
            SoftChip(
                text = skill,
                selected = selected,
                onClick = {}
            )
        }
    }
}

@Composable
private fun RoadmapCard(roadmap: List<RoadmapStep>) {
    SoftCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.improvement_roadmap),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(14.dp))

        val visibleSteps = roadmap.take(3)
        visibleSteps.forEachIndexed { index, step ->
            RoadmapStepCard(step = step, isLast = index == visibleSteps.lastIndex)
            if (index != visibleSteps.lastIndex) Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun RoadmapStepCard(
    step: RoadmapStep,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            if (!isLast) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(70.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        SoftCard(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(14.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        ) {
            Text(
                text = step.phase,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))
            step.items.take(3).forEach {
                BulletItem(text = it)
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun SuggestedSummaryCard(summary: String) {
    SoftCard(Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.suggested_summary),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = summary,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun BulletItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun queryName(
    cr: android.content.ContentResolver,
    uri: android.net.Uri
): String? {
    val c = cr.query(uri, null, null, null, null) ?: return null
    return c.use {
        val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst() && idx >= 0) it.getString(idx) else null
    }
}