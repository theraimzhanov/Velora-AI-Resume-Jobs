package com.velora.mobile.domain.jobs

data class JobApplication(
    val id: String = "",
    val company: String = "",
    val position: String = "",
    val status: String = ApplicationStatus.Applied.name,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)