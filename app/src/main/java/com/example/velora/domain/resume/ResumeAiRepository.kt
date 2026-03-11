package com.example.velora.domain.resume

import android.net.Uri

interface ResumeAiRepository {
    suspend fun analyzeResumePdf(
        fileBytes: ByteArray,
        mimeType: String,
        jobTarget: String? = null
    ): ResumeReport
}