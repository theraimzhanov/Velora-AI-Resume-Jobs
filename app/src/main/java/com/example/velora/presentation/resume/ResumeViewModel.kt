package com.example.velora.presentation.resume


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.velora.domain.resume.ResumeAnalysis
import com.example.velora.domain.resume.ResumeAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ResumeUiState(
    val input: String = "",
    val loading: Boolean = false,
    val result: ResumeAnalysis? = null,
    val error: String? = null
)

@HiltViewModel
class ResumeViewModel @Inject constructor(
    private val analyzer: ResumeAnalyzer
) : ViewModel() {

    private val _ui = MutableStateFlow(ResumeUiState())
    val ui: StateFlow<ResumeUiState> = _ui.asStateFlow()

    fun setInput(v: String) = _ui.update { it.copy(input = v, error = null) }

    fun analyze() {
        val text = _ui.value.input.trim()
        if (text.length < 60) {
            _ui.update { it.copy(error = "Paste more resume text (at least a few paragraphs).") }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null, result = null) }
            runCatching { analyzer.analyze(text) }
                .onFailure { e -> _ui.update { it.copy(loading = false, error = e.message ?: "Failed") } }
                .onSuccess { r -> _ui.update { it.copy(loading = false, result = r) } }
        }
    }
}