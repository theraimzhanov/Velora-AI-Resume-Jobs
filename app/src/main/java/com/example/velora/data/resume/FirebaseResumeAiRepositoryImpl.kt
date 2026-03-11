package com.example.velora.data.resume

import com.example.velora.domain.resume.ResumeAiRepository
import com.example.velora.domain.resume.ResumeReport
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import org.json.JSONObject

// Firebase AI types (your IDE will help auto-import the correct ones)
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.InlineDataPart
import com.google.firebase.ai.type.TextPart

class FirebaseResumeAiRepositoryImpl : ResumeAiRepository {

    override suspend fun analyzeResumePdf(
        fileBytes: ByteArray,
        mimeType: String,
        jobTarget: String?
    ): ResumeReport {

        val model = Firebase.ai.generativeModel("gemini-2.5-flash-lite")

        val prompt = buildString {
            appendLine("You are an expert resume reviewer and ATS specialist.")
            appendLine("Return ONLY valid JSON in exactly this schema (no markdown, no extra text):")
            appendLine(
                """
                {
                  "overallScore": 0,
                  "headline": "",
                  "strengths": [],
                  "weaknesses": [],
                  "quickFixes": [],
                  "keywordGaps": [],
                  "atsNotes": [],
                  "suggestedSummary": ""
                }
                """.trimIndent()
            )
            jobTarget?.takeIf { it.isNotBlank() }?.let { appendLine("Target role: $it") }
        }

        // -----------------------------
        // ✅ Build content (pick the variant that compiles in YOUR SDK)
        // -----------------------------

        // ===== Variant A (common): Content(role, parts)
        val content = Content(
            role = "user",
            parts = listOf(
                TextPart(prompt),
                InlineDataPart(fileBytes, mimeType)
            )
        )

        // ===== Variant B (if Variant A doesn't compile):
        // val content = Content.Builder()
        //     .setRole("user")
        //     .setParts(listOf(TextPart(prompt), InlineDataPart(fileBytes, mimeType)))
        //     .build()

        // ===== Variant C (if both above don't compile):
        // val content = Content(
        //     listOf(TextPart(prompt), InlineDataPart(fileBytes, mimeType))
        // )

        val response = model.generateContent(content)

        val raw = response.text?.trim().orEmpty()
        val jsonOnly = extractJsonObject(raw)
            ?: error("AI did not return JSON. Raw response:\n$raw")

        return parseReport(jsonOnly)
    }

    private fun extractJsonObject(text: String): String? {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start == -1 || end == -1 || end <= start) return null
        return text.substring(start, end + 1).trim()
    }

    private fun parseReport(json: String): ResumeReport {
        val o = JSONObject(json)

        fun arr(name: String): List<String> =
            o.optJSONArray(name)?.let { a ->
                List(a.length()) { idx -> a.optString(idx) }.filter { it.isNotBlank() }
            } ?: emptyList()

        return ResumeReport(
            overallScore = o.optInt("overallScore", 0),
            headline = o.optString("headline", ""),
            strengths = arr("strengths"),
            weaknesses = arr("weaknesses"),
            quickFixes = arr("quickFixes"),
            keywordGaps = arr("keywordGaps"),
            atsNotes = arr("atsNotes"),
            suggestedSummary = o.optString("suggestedSummary", "")
        )
    }
}