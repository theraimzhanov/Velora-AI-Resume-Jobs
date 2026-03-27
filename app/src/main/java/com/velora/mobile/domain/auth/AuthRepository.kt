package com.velora.mobile.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<AuthState>
    suspend fun login(email: String, password: String)
    suspend fun register(email: String, password: String)
    suspend fun logout()

    suspend fun loginWithGoogleIdToken(idToken: String)
    suspend fun sendPasswordReset(email: String)
}