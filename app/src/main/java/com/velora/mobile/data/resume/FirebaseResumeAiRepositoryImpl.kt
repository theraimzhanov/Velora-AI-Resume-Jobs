package com.velora.mobile.data.resume

import com.velora.mobile.domain.resume.ResumeAiRepository
import com.velora.mobile.domain.resume.ResumeReport
import com.velora.mobile.domain.resume.RoadmapStep
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlin.math.max
import kotlin.math.min
import org.json.JSONArray
import org.json.JSONObject

class FirebaseResumeAiRepositoryImpl : ResumeAiRepository {

    override suspend fun analyzeResume(
        extractedText: String,
        fileName: String?,
        mimeType: String,
        jobTarget: String?,
        outputLanguage: String
    ): ResumeReport {

        val model = Firebase.ai.generativeModel("gemini-2.5-flash")

        val prompt = buildPrompt(
            extractedText = extractedText,
            fileName = fileName,
            mimeType = mimeType,
            jobTarget = jobTarget,
            outputLanguage = outputLanguage
        )

        val response = model.generateContent(prompt)
        val raw = response.text?.trim().orEmpty()

        val jsonOnly = extractJsonObject(raw)
            ?: error("AI did not return valid JSON.")

        val report = parseReport(jsonOnly)

        return applyRoleMismatchPenalty(
            report = report,
            resumeText = extractedText,
            targetRole = jobTarget
        )
    }

    private fun buildPrompt(
        extractedText: String,
        fileName: String?,
        mimeType: String,
        jobTarget: String?,
        outputLanguage: String
    ): String {
        val safeText = extractedText.take(18000)
        val target = jobTarget ?: "Not provided"

        return """
You are a strict recruiter, ATS reviewer, and career coach.

Analyze this resume or CV for real hiring quality.

IMPORTANT:
- Be realistic, not nice.
- Keep text short and informative.
- Avoid long sentences.
- Most user-facing strings should be 1 short sentence.
- Bullet items should be concise.
- If target role is very different from the resume, targetFitScore must be low.
- If recruiter may skip this resume, say why clearly.
- Explain format in a short way.
- Explain length in a short way.
- Return all user-facing text in: $outputLanguage
- Return ONLY valid JSON.

Use exactly this JSON schema:
{
  "overallScore": 0,
  "targetFitScore": 0,
  "headline": "",
  "recruiterVerdict": "",
  "formatAssessment": "",
  "lengthAssessment": "",
  "targetRoleAssessment": "",
  "redFlags": [],
  "recruiterConcerns": [],
  "strengths": [],
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

Scoring rules:
- overallScore = quality of resume itself
- targetFitScore = fit for target role
- strong mismatch => targetFitScore usually below 35
- do not inflate scores

Good short examples:
- formatAssessment: "Clean and easy to scan."
- formatAssessment: "Weak structure. Key sections are hard to find."
- lengthAssessment: "Too short for a competitive role."
- lengthAssessment: "A bit long. Remove low-value details."
- targetRoleAssessment: "Good fit for junior Android roles."
- targetRoleAssessment: "Weak fit for farming roles."

File name: ${fileName.orEmpty()}
Mime type: $mimeType
Target role: $target
Text length: ${extractedText.length}

Resume / CV text:
$safeText
        """.trimIndent()
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
                List(a.length()) { i -> a.optString(i) }.filter { it.isNotBlank() }
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
            overallScore = o.optInt("overallScore", 0).coerceIn(0, 100),
            targetFitScore = o.optInt("targetFitScore", 0).coerceIn(0, 100),
            headline = o.optString("headline", ""),
            recruiterVerdict = o.optString("recruiterVerdict", ""),
            formatAssessment = o.optString("formatAssessment", ""),
            lengthAssessment = o.optString("lengthAssessment", ""),
            targetRoleAssessment = o.optString("targetRoleAssessment", ""),
            redFlags = arr("redFlags"),
            recruiterConcerns = arr("recruiterConcerns"),
            strengths = arr("strengths"),
            quickFixes = arr("quickFixes"),
            detectedSkills = arr("detectedSkills"),
            missingSkills = arr("missingSkills"),
            keywordGaps = arr("keywordGaps"),
            atsNotes = arr("atsNotes"),
            roadmap = roadmap(),
            suggestedSummary = o.optString("suggestedSummary", "")
        )
    }

    private fun applyRoleMismatchPenalty(
        report: ResumeReport,
        resumeText: String,
        targetRole: String?
    ): ResumeReport {
        if (targetRole.isNullOrBlank()) return report

        val resumeDomain = detectDomain(resumeText)
        val targetDomain = detectDomain(targetRole)

        if (resumeDomain == null || targetDomain == null) return report
        if (resumeDomain == targetDomain) return report

        val hardMismatch = isHardMismatch(resumeDomain, targetDomain)
        if (!hardMismatch) return report

        val newFit = min(report.targetFitScore, 25)
        val newOverall = min(report.overallScore, 48)

        val concern =
            "Resume looks closer to $resumeDomain, but target role is closer to $targetDomain."
        val redFlag =
            "Strong mismatch between resume background and target role."

        return report.copy(
            overallScore = max(18, newOverall),
            targetFitScore = newFit,
            targetRoleAssessment = concern,
            redFlags = (listOf(redFlag) + report.redFlags).distinct().take(6),
            recruiterConcerns = (listOf(concern) + report.recruiterConcerns).distinct().take(6),
            recruiterVerdict = if (report.recruiterVerdict.isBlank()) {
                "A recruiter may reject this because the role match is weak."
            } else {
                report.recruiterVerdict
            }
        )
    }

    private fun detectDomain(text: String): String? {
        val t = text.lowercase()

        val domains = linkedMapOf(
            "software engineering" to listOf(
                "android", "kotlin", "java", "developer", "engineer", "compose",
                "firebase", "mvvm", "api", "github", "programming"
            ),
            "data" to listOf(
                "sql", "python", "data analyst", "machine learning", "tableau", "dashboard"
            ),
            "design" to listOf(
                "ui", "ux", "figma", "designer", "prototype"
            ),
            "marketing" to listOf(
                "marketing", "seo", "campaign", "content", "brand"
            ),
            "sales" to listOf(
                "sales", "client", "pipeline", "lead generation"
            ),
            "finance" to listOf(
                "finance", "accounting", "auditing", "financial"
            ),
            "education" to listOf(
                "teacher", "teaching", "education", "curriculum", "tutor"
            ),
            "healthcare" to listOf(
                "nurse", "medical", "patient", "clinic"
            ),
            "agriculture" to listOf(
                "farmer", "farming", "agriculture", "crop", "livestock", "harvest"
            )
        )

        val scored = domains.mapValues { (_, keywords) ->
            keywords.count { keyword -> t.contains(keyword) }
        }

        val best = scored.maxByOrNull { it.value } ?: return null
        return if (best.value >= 2) best.key else null
    }

    private fun isHardMismatch(resumeDomain: String, targetDomain: String): Boolean {
        if (resumeDomain == targetDomain) return false

        val softTransitions = setOf(
            "software engineering:data",
            "data:software engineering",
            "marketing:sales",
            "sales:marketing",
            "finance:data",
            "data:finance"
        )

        return "$resumeDomain:$targetDomain" !in softTransitions
    }
}