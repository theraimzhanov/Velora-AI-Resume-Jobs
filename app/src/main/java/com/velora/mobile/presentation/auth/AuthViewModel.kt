package com.velora.mobile.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velora.mobile.core.UiEvents
import com.velora.mobile.core.isValidEmail
import com.velora.mobile.data.auth.GoogleIdTokenProvider
import com.velora.mobile.domain.auth.AuthRepository
import com.velora.mobile.domain.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false
) {
    val canSubmit: Boolean
        get() = email.trim().isValidEmail() && password.length >= 6 && !loading

    val canResetPassword: Boolean
        get() = email.trim().isValidEmail() && !loading
}

sealed interface AuthEvent {
    data class Error(val msg: String) : AuthEvent
    data class Success(val msg: String) : AuthEvent
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val google: GoogleIdTokenProvider
) : ViewModel() {

    val authState: StateFlow<AuthState> =
        repo.authState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthState.Loading
        )

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    val events = UiEvents<AuthEvent>()

    fun setEmail(v: String) = _ui.update { it.copy(email = v.trim()) }
    fun setPassword(v: String) = _ui.update { it.copy(password = v) }

    fun login() = runAuth { repo.login(ui.value.email.trim(), ui.value.password) }
    fun register() = runAuth { repo.register(ui.value.email.trim(), ui.value.password) }
    fun logout() = viewModelScope.launch { repo.logout() }

    fun loginWithGoogle(context: Context) = runAuth {
        val idToken = google.getIdToken(context)
        repo.loginWithGoogleIdToken(idToken)
    }

    fun sendPasswordReset() {
        val email = ui.value.email.trim()

        if (!email.isValidEmail()) {
            viewModelScope.launch {
                events.send(AuthEvent.Error("Enter a valid email address"))
            }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(loading = true) }

            runCatching {
                repo.sendPasswordReset(email)
            }.onSuccess {
                events.send(AuthEvent.Success("Password reset email sent"))
            }.onFailure { e ->
                events.send(AuthEvent.Error(e.message ?: "Failed to send reset email"))
            }

            _ui.update { it.copy(loading = false) }
        }
    }

    private fun runAuth(block: suspend () -> Unit) {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true) }

            runCatching { block() }
                .onFailure { e ->
                    val msg = e.message ?: "Authentication failed"
                    events.send(AuthEvent.Error(msg))
                }

            _ui.update { it.copy(loading = false) }
        }
    }
}