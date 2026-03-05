package com.example.velora.data.resume


import com.example.velora.domain.resume.ResumeAnalysis
import com.example.velora.domain.resume.ResumeAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalResumeAnalyzer : ResumeAnalyzer {

    override suspend fun analyze(text: String): ResumeAnalysis = withContext(Dispatchers.Default) {
        val raw = text.trim()
        val lower = raw.lowercase()

        val hasEmail = Regex("""[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}""", RegexOption.IGNORE_CASE).containsMatchIn(raw)
        val hasPhone = Regex("""(\+?\d[\d\s().-]{7,}\d)""").containsMatchIn(raw)

        val sections = listOf("experience","projects","education","skills","summary")
        val sectionHits = sections.count { lower.contains(it) }

        val keywords = listOf("kotlin","android","compose","mvvm","hilt","firebase","coroutines","flow","retrofit","room")
        val keywordHits = keywords.count { lower.contains(it) }

        val issues = buildList {
            if (!hasEmail) add("Missing email.")
            if (!hasPhone) add("Missing phone number.")
            if (sectionHits < 3) add("Missing standard sections (Experience/Projects/Education/Skills).")
            if (keywordHits < 4) add("Low ATS keyword coverage for Android roles.")
            if (raw.length < 900) add("Too short; add impact bullets with metrics.")
        }

        val strengths = buildList {
            if (hasEmail) add("Email detected.")
            if (hasPhone) add("Phone detected.")
            if (sectionHits >= 4) add("Strong structure with standard sections.")
            if (keywordHits >= 6) add("Good Android keyword coverage.")
        }

        val suggestions = buildList {
            add("Use impact bullets: Action + Tool + Result (+ numbers).")
            add("Add a tailored Summary (2–3 lines) for your target role.")
            add("For each project: stack + what improved + measurable outcome.")
            if (keywordHits < 6) add("Add missing keywords naturally: Compose, Flow, Hilt, Firebase, MVVM.")
            if (!lower.contains("github")) add("Add GitHub links to projects.")
        }

        val score = (100 - issues.size * 10 + keywordHits * 3 + sectionHits * 4).coerceIn(5, 98)

        ResumeAnalysis(score, strengths, issues, suggestions)
    }
}