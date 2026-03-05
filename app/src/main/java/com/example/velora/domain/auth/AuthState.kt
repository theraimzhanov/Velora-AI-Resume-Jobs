package com.example.velora.domain.auth

sealed interface AuthState {
    data object Loading : AuthState
    data object SignedOut : AuthState
    data class SignedIn(val uid: String, val email: String?) : AuthState
}