package com.example.velora.presentation.resume

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.velora.domain.resume.ResumeAiRepository
import com.example.velora.domain.resume.ResumeReport
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ResumeUiState(
    val fileUri: Uri? = null,
    val fileName: String? = null,
    val loading: Boolean = false,
    val progressLabel: String? = null,
    val report: ResumeReport? = null,
    val error: String? = null,
    val jobTarget: String = ""
)

@HiltViewModel
class ResumeViewModel @Inject constructor(
    private val repo: ResumeAiRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ResumeUiState())
    val ui: StateFlow<ResumeUiState> = _ui.asStateFlow()

    fun setJobTarget(v: String) = _ui.update { it.copy(jobTarget = v) }

    fun onFilePicked(uri: Uri, fileName: String?) {
        _ui.update { it.copy(fileUri = uri, fileName = fileName, report = null, error = null) }
    }

    fun analyze(contentResolver: ContentResolver) {
        val uri = ui.value.fileUri ?: return

        viewModelScope.launch {
            _ui.update { it.copy(loading = true, progressLabel = "Reading file…", error = null, report = null) }

            runCatching {
                val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: error("Failed to read file")

                _ui.update { it.copy(progressLabel = "Analyzing with AI…") }

                // For now assume PDF. We'll add DOCX extraction after this works.
                repo.analyzeResumePdf(
                    fileBytes = bytes,
                    mimeType = "application/pdf",
                    jobTarget = ui.value.jobTarget
                )
            }.onSuccess { report ->
                _ui.update { it.copy(loading = false, progressLabel = null, report = report) }
            }.onFailure { e ->
                _ui.update { it.copy(loading = false, progressLabel = null, error = e.message ?: "Analysis failed") }
            }
        }
    }
}