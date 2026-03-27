package com.velora.mobile.presentation.resume

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velora.mobile.core.language.LocaleManager
import com.velora.mobile.data.resume.ResumeDocumentTextExtractor
import com.velora.mobile.domain.resume.ResumeAiRepository
import com.velora.mobile.domain.resume.ResumeReport
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
    val analysisLanguageInput: String = "",
    val appLanguageCode: String = "en",
    val appLanguageName: String = "English"
)

@HiltViewModel
class ResumeViewModel @Inject constructor(
    private val repo: ResumeAiRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(
        ResumeUiState().copy(
            appLanguageCode = LocaleManager.currentLanguageCode(),
            appLanguageName = languageNameFromCode(LocaleManager.currentLanguageCode())
        )
    )
    val ui: StateFlow<ResumeUiState> = _ui.asStateFlow()

    private fun languageNameFromCode(code: String): String {
        return when (code.lowercase()) {
            "ru" -> "Russian"
            "es" -> "Spanish"
            else -> "English"
        }
    }

    fun refreshLanguage() {
        val code = LocaleManager.currentLanguageCode()
        _ui.update {
            it.copy(
                appLanguageCode = code,
                appLanguageName = languageNameFromCode(code)
            )
        }
    }

    fun setJobTarget(v: String) {
        _ui.update { it.copy(jobTarget = v) }
    }

    fun setAnalysisLanguageInput(v: String) {
        _ui.update { it.copy(analysisLanguageInput = v) }
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

        val resolvedLanguage = state.analysisLanguageInput.trim().ifBlank {
            state.appLanguageName.ifBlank { "English" }
        }

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

                _ui.update {
                    it.copy(
                        progressLabel = "Running AI recruiter audit in $resolvedLanguage…"
                    )
                }

                repo.analyzeResume(
                    extractedText = text,
                    fileName = state.fileName,
                    mimeType = mimeType,
                    jobTarget = state.jobTarget.trim().ifBlank { null },
                    outputLanguage = resolvedLanguage
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