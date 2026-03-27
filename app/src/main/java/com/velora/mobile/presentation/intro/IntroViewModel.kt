package com.velora.mobile.presentation.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velora.mobile.data.prefs.IntroPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val prefs: IntroPrefs
) : ViewModel() {
    val introDone: StateFlow<Boolean> =
        prefs.introDone.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun finishIntro() {
        viewModelScope.launch { prefs.setIntroDone(true) }
    }
}