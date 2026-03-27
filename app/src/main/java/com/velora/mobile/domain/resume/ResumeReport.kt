package com.velora.mobile.domain.resume

import kotlinx.serialization.Serializable

@Serializable
data class ResumeReport(
    val overallScore: Int,
    val targetFitScore: Int,

    val headline: String,
    val recruiterVerdict: String,

    val formatAssessment: String,
    val lengthAssessment: String,
    val targetRoleAssessment: String,

    val redFlags: List<String>,
    val recruiterConcerns: List<String>,
    val strengths: List<String>,
    val quickFixes: List<String>,

    val detectedSkills: List<String>,
    val missingSkills: List<String>,
    val keywordGaps: List<String>,
    val atsNotes: List<String>,

    val roadmap: List<RoadmapStep>,
    val suggestedSummary: String
)

@Serializable
data class RoadmapStep(
    val phase: String,
    val title: String,
    val items: List<String>
)