package com.clicks.app.onboarding.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

sealed interface SplashState {
    data object Loading : SplashState
    data object Ready : SplashState
}

class SplashViewModel : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)

    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            // Simulate startup initialization (checking session, config, etc.)
            delay(1500.milliseconds)
            _state.value = SplashState.Ready
        }
    }
}
