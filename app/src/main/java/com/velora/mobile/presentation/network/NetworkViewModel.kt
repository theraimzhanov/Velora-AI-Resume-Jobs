package com.velora.mobile.presentation.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velora.mobile.core.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NetworkUiState(
    val isConnected: Boolean = true,
    val checkedAtLeastOnce: Boolean = false
)

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _ui = MutableStateFlow(NetworkUiState())
    val ui: StateFlow<NetworkUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            networkMonitor.observe().collect { connected ->
                _ui.update {
                    it.copy(
                        isConnected = connected,
                        checkedAtLeastOnce = true
                    )
                }
            }
        }
    }

    fun retryNow() {
        _ui.update {
            it.copy(
                isConnected = networkMonitor.isCurrentlyConnected(),
                checkedAtLeastOnce = true
            )
        }
    }
}