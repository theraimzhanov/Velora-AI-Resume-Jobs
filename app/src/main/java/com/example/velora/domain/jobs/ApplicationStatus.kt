package com.example.velora.domain.jobs

import androidx.annotation.StringRes
import com.example.velora.R

enum class ApplicationStatus(@StringRes val labelRes: Int) {
    Applied(R.string.applied),
    Interview(R.string.interview),
    Offer(R.string.offer),
    Rejected(R.string.rejected);

    companion object {
        fun fromName(v: String?): ApplicationStatus =
            entries.firstOrNull { it.name.equals(v, ignoreCase = true) } ?: Applied
    }
}