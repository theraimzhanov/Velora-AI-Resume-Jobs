package com.example.velora.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.velora.core.UiEvents
import com.example.velora.core.isValidEmail
import com.example.velora.data.auth.GoogleIdTokenProvider
import com.example.velora.domain.auth.AuthRepository
import com.example.velora.domain.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false
) {
    // Trim email for safety (prevents "space" bug)
    val canSubmit: Boolean get() = email.trim().isValidEmail() && password.length >= 6 && !loading
}

sealed interface AuthEvent { data class Error(val msg: String) : AuthEvent }

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

    /**
     * Google Sign-In (Firebase Auth)
     * Call from UI: vm.loginWithGoogle(LocalContext.current)
     */
    fun loginWithGoogle(context: Context) = runAuth {
        val idToken = google.getIdToken(context)
        repo.loginWithGoogleIdToken(idToken)
    }

    private fun runAuth(block: suspend () -> Unit) {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true) }

            runCatching { block() }
                .onFailure { e ->
                    // Clean user-facing message
                    val msg = e.message ?: "Authentication failed"
                    events.send(AuthEvent.Error(msg))
                }

            _ui.update { it.copy(loading = false) }
        }
    }
}