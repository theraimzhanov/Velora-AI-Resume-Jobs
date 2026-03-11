package com.example.velora.presentation.tracker


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.velora.core.clean
import com.example.velora.domain.auth.AuthState
import com.example.velora.domain.jobs.JobApplication
import com.example.velora.domain.jobs.JobsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TrackerUiState(
    val uid: String? = null,
    val loading: Boolean = true,
    val jobs: List<JobApplication> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val repo: JobsRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(TrackerUiState())
    val ui: StateFlow<TrackerUiState> = _ui.asStateFlow()

    private var observing = false

    fun bind(auth: AuthState) {
        val uid = (auth as? AuthState.SignedIn)?.uid
        _ui.update { it.copy(uid = uid) }

        if (!observing && uid != null) {
            observing = true
            viewModelScope.launch {
                _ui.update { it.copy(loading = true, error = null) }
                repo.observe(uid)
                    .catch { e -> _ui.update { it.copy(loading = false, error = e.message ?: "Failed") } }
                    .collect { list -> _ui.update { it.copy(loading = false, jobs = list) } }
            }
        }
    }

    fun add(company: String, position: String, status: String) {
        val uid = _ui.value.uid ?: run {
            _ui.update { it.copy(error = "Please sign in first.") }
            return
        }
        val c = company.clean()
        val p = position.clean()
        if (c.isBlank() || p.isBlank()) return

        viewModelScope.launch {
            runCatching { repo.add(uid, c, p, status) }
                .onFailure { e -> _ui.update { it.copy(error = e.message ?: "Could not add") } }
        }
    }

    fun setStatus(id: String, status: String) {
        val uid = _ui.value.uid ?: return
        viewModelScope.launch { repo.setStatus(uid, id, status) }
    }

    fun delete(id: String) {
        val uid = _ui.value.uid ?: return
        viewModelScope.launch { repo.delete(uid, id) }
    }
}