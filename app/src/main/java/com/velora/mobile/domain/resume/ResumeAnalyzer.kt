package com.velora.mobile.domain.resume

interface ResumeAnalyzer {
    suspend fun analyze(text: String): ResumeAnalysis
}