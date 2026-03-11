package com.example.velora.domain.jobs

enum class ApplicationStatus(val label: String) {
    Applied("Applied"),
    Interview("Interview"),
    Offer("Offer"),
    Rejected("Rejected");

    companion object {
        fun fromLabel(v: String?): ApplicationStatus =
            entries.firstOrNull { it.label.equals(v, ignoreCase = true) } ?: Applied
    }
}