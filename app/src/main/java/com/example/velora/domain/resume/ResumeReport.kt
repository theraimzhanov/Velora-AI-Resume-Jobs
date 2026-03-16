package com.example.velora.domain.resume

import kotlinx.serialization.Serializable

@Serializable
data class ResumeReport(
    val overallScore: Int,
    val headline: String,

    val redFlags: List<String>,
    val strengths: List<String>,
    val weaknesses: List<String>,
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
    val phase: String,          // "Today", "This Week", "Next 30 Days"
    val title: String,
    val items: List<String>
)