package com.example.velora.domain.resume

data class ResumeAnalysis(
    val score: Int,
    val strengths: List<String>,
    val issues: List<String>,
    val suggestions: List<String>
)