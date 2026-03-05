package com.example.velora.domain.resume

interface ResumeAnalyzer {
    suspend fun analyze(text: String): ResumeAnalysis
}