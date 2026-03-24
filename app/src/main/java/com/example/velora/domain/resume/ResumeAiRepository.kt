package com.example.velora.domain.resume

interface ResumeAiRepository {
    suspend fun analyzeResume(
        extractedText: String,
        fileName: String?,
        mimeType: String,
        jobTarget: String? = null,
        outputLanguage: String = "English"
    ): ResumeReport
}