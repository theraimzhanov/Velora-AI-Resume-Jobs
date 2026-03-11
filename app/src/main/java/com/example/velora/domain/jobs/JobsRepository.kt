package com.example.velora.domain.jobs

import kotlinx.coroutines.flow.Flow

interface JobsRepository {
    fun observe(uid: String): Flow<List<JobApplication>>
    suspend fun add(uid: String, company: String, position: String,string: String)
    suspend fun setStatus(uid: String, id: String, status: String)
    suspend fun delete(uid: String, id: String)
}