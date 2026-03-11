package com.example.velora.domain.resume

import kotlinx.serialization.Serializable

@Serializable
data class ResumeReport(
    val overallScore: Int,                 // 0..100
    val headline: String,                  // 1 sentence
    val strengths: List<String>,
    val weaknesses: List<String>,
    val quickFixes: List<String>,          // actionable bullets
    val keywordGaps: List<String>,         // missing skills/keywords
    val atsNotes: List<String>,            // ATS-specific notes
    val suggestedSummary: String           // improved summary line
)