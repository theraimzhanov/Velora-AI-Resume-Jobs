package com.velora.mobile.presentation.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velora.mobile.data.prefs.IntroPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val prefs: IntroPrefs
) : ViewModel() {

    val introDone: StateFlow<Boolean> =
        prefs.introDone.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun finishIntro() {
        viewModelScope.launch {
            prefs.setIntroDone(true)
        }
    }
}