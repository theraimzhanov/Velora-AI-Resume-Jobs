package com.example.velora.presentation.resume

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.velora.data.resume.ResumeDocumentTextExtractor
import com.example.velora.domain.resume.ResumeAiRepository
import com.example.velora.domain.resume.ResumeReport
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResumeUiState(
    val fileUri: Uri? = null,
    val fileName: String? = null,
    val mimeType: String? = null,
    val loading: Boolean = false,
    val progressLabel: String? = null,
    val report: ResumeReport? = null,
    val error: String? = null,
    val jobTarget: String = "",
    val analysisLanguage: String = ""
)

@HiltViewModel
class ResumeViewModel @Inject constructor(
    private val repo: ResumeAiRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ResumeUiState())
    val ui: StateFlow<ResumeUiState> = _ui.asStateFlow()

    fun setJobTarget(v: String) {
        _ui.update { it.copy(jobTarget = v) }
    }

    fun setAnalysisLanguage(v: String) {
        _ui.update { it.copy(analysisLanguage = v) }
    }

    fun onFilePicked(uri: Uri, fileName: String?, mimeType: String?) {
        _ui.update {
            it.copy(
                fileUri = uri,
                fileName = fileName,
                mimeType = mimeType,
                report = null,
                error = null
            )
        }
    }

    fun clearError() {
        _ui.update { it.copy(error = null) }
    }

    fun analyze(contentResolver: ContentResolver) {
        val state = ui.value
        val uri = state.fileUri ?: return
        val mimeType = state.mimeType.orEmpty()

        viewModelScope.launch {
            _ui.update {
                it.copy(
                    loading = true,
                    progressLabel = "Reading resume / CV…",
                    report = null,
                    error = null
                )
            }

            runCatching {
                val text = ResumeDocumentTextExtractor.extractText(
                    contentResolver = contentResolver,
                    uri = uri,
                    mimeType = mimeType,
                    fileName = state.fileName
                )

                _ui.update { it.copy(progressLabel = "Running AI recruiter audit…") }

                repo.analyzeResume(
                    extractedText = text,
                    fileName = state.fileName,
                    mimeType = mimeType,
                    jobTarget = state.jobTarget.trim().ifBlank { null },
                    outputLanguage = state.analysisLanguage.trim().ifBlank { "English" }
                )
            }.onSuccess { report ->
                _ui.update {
                    it.copy(
                        loading = false,
                        progressLabel = null,
                        report = report
                    )
                }
            }.onFailure { e ->
                _ui.update {
                    it.copy(
                        loading = false,
                        progressLabel = null,
                        error = e.message ?: "Analysis failed"
                    )
                }
            }
        }
    }
}