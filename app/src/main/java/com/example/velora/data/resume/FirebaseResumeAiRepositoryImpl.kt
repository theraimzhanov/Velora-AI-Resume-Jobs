package com.example.velora.data.resume

import com.example.velora.domain.resume.ResumeAiRepository
import com.example.velora.domain.resume.ResumeReport
import com.example.velora.domain.resume.RoadmapStep
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import org.json.JSONObject
import org.json.JSONArray

class FirebaseResumeAiRepositoryImpl : ResumeAiRepository {

    override suspend fun analyzeResume(
        extractedText: String,
        fileName: String?,
        mimeType: String,
        jobTarget: String?
    ): ResumeReport {

        val model = Firebase.ai.generativeModel("gemini-2.5-flash-lite")

        val prompt = buildString {
            appendLine("You are a senior resume reviewer, ATS specialist, and career coach.")
            appendLine("Analyze the resume text below for real-world hiring quality.")
            appendLine("Be practical, specific, and recruiter-oriented.")
            appendLine()
            appendLine("Return ONLY valid JSON. No markdown. No explanation outside JSON.")
            appendLine("Use exactly this schema:")
            appendLine(
                """
                {
                  "overallScore": 0,
                  "headline": "",
                  "redFlags": [],
                  "strengths": [],
                  "weaknesses": [],
                  "quickFixes": [],
                  "detectedSkills": [],
                  "missingSkills": [],
                  "keywordGaps": [],
                  "atsNotes": [],
                  "roadmap": [
                    {
                      "phase": "",
                      "title": "",
                      "items": []
                    }
                  ],
                  "suggestedSummary": ""
                }
                """.trimIndent()
            )
            appendLine()
            appendLine("Scoring guidance:")
            appendLine("- 85-100 = strong and competitive")
            appendLine("- 70-84 = good but needs improvement")
            appendLine("- 50-69 = weak for competitive roles")
            appendLine("- below 50 = major resume problems")
            appendLine()
            jobTarget?.takeIf { it.isNotBlank() }?.let {
                appendLine("Target role: $it")
            }
            appendLine("File name: ${fileName.orEmpty()}")
            appendLine("Mime type: $mimeType")
            appendLine()
            appendLine("Resume text:")
            appendLine(extractedText.take(18000))
        }

        val response = model.generateContent(prompt)
        val raw = response.text?.trim().orEmpty()

        val jsonOnly = extractJsonObject(raw)
            ?: error("AI did not return valid JSON.")

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

        fun roadmap(): List<RoadmapStep> {
            val a = o.optJSONArray("roadmap") ?: return emptyList()
            return List(a.length()) { index ->
                val item = a.optJSONObject(index) ?: JSONObject()
                val list = item.optJSONArray("items") ?: JSONArray()
                RoadmapStep(
                    phase = item.optString("phase"),
                    title = item.optString("title"),
                    items = List(list.length()) { i -> list.optString(i) }.filter { it.isNotBlank() }
                )
            }
        }

        return ResumeReport(
            overallScore = o.optInt("overallScore", 0),
            headline = o.optString("headline", ""),
            redFlags = arr("redFlags"),
            strengths = arr("strengths"),
            weaknesses = arr("weaknesses"),
            quickFixes = arr("quickFixes"),
            detectedSkills = arr("detectedSkills"),
            missingSkills = arr("missingSkills"),
            keywordGaps = arr("keywordGaps"),
            atsNotes = arr("atsNotes"),
            roadmap = roadmap(),
            suggestedSummary = o.optString("suggestedSummary", "")
        )
    }
}