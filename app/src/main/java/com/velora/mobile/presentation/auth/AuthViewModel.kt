
package com.velora.mobile.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.velora.mobile.core.UiEvents
import com.velora.mobile.core.isValidEmail
import com.velora.mobile.data.auth.GoogleIdTokenProvider
import com.velora.mobile.domain.auth.AuthRepository
import com.velora.mobile.domain.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    fun setEmail(value: String) {
        _ui.update { it.copy(email = value.trim()) }
    }

    fun setPassword(value: String) {
        _ui.update { it.copy(password = value) }
    }

    fun login() = runAuth {
        repo.login(ui.value.email.trim(), ui.value.password)
    }

    fun register() = runAuth {
        repo.register(ui.value.email.trim(), ui.value.password)
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
        }
    }

    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true) }

            try {
                val idToken = google.getIdToken(context)

                when (authState.value) {
                    is AuthState.SignedIn -> {
                        try {
                            repo.linkGoogleIdToken(idToken)
                            events.send(AuthEvent.Success("Google account linked successfully"))
                        } catch (e: FirebaseAuthUserCollisionException) {
                            repo.loginWithGoogleIdToken(idToken)
                        } catch (_: Exception) {
                            events.send(AuthEvent.Error("Something went wrong, try again!"))
                        }
                    }

                    else -> {
                        try {
                            repo.loginWithGoogleIdToken(idToken)
                        } catch (_: FirebaseAuthUserCollisionException) {
                            events.send(
                                AuthEvent.Error(
                                    "This email already uses password sign-in. Sign in with email first, then connect Google."
                                )
                            )
                        } catch (_: Exception) {
                            events.send(AuthEvent.Error("Something went wrong, try again!"))
                        }
                    }
                }
            } catch (_: Exception) {
                events.send(AuthEvent.Error("Something went wrong, try again!"))
            }

            _ui.update { it.copy(loading = false) }
        }
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
            }.onFailure {
                events.send(AuthEvent.Error("Something went wrong, try again!"))
            }

            _ui.update { it.copy(loading = false) }
        }
    }

    private fun runAuth(block: suspend () -> Unit) {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true) }

            runCatching {
                block()
            }.onFailure {
                events.send(AuthEvent.Error("Something went wrong, try again!"))
            }

            _ui.update { it.copy(loading = false) }
        }
    }
}