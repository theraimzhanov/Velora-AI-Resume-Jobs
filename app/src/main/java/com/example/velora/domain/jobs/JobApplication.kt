package com.example.velora.domain.jobs

data class JobApplication(
    val id: String = "",
    val company: String = "",
    val position: String = "",
    val status: String = "Applied",
    val createdAt: Long = 0L
)