package com.clicks.app.feature.auth.ui.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.feature.auth.data.AuthRepository
import com.clicks.app.feature.auth.data.MockPasswordRecoveryRepository
import com.clicks.app.feature.auth.data.PasswordRecoveryRepository
import com.clicks.app.lib.validation.AuthValidators
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

enum class RecoveryStep { Email, Code, NewPassword, Done }

data class PasswordRecoveryUiState(
    val step: RecoveryStep = RecoveryStep.Email,
    val email: String = "",
    val isLoading: Boolean = false,
    val fieldError: String? = null,
    val confirmError: String? = null,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val resendSecondsLeft: Int = 0
)

class PasswordRecoveryViewModel(
    private val repository: PasswordRecoveryRepository = MockPasswordRecoveryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordRecoveryUiState())
    val uiState: StateFlow<PasswordRecoveryUiState> = _uiState.asStateFlow()

    // Kept out of UI state on purpose: never rendered, never saved.
    private var resetToken: String? = null
    private var cooldownJob: Job? = null

    fun submitEmail(rawEmail: String) {
        if (_uiState.value.isLoading) return

        val email = rawEmail.trim()
        if (!AuthValidators.isValidEmail(email)) {
            _uiState.update { it.copy(fieldError = "Valid email is required") }
            return
        }

        _uiState.update { it.copy(isLoading = true, fieldError = null, errorMessage = null) }

        viewModelScope.launch {
            when (val result = repository.requestPasswordReset(email)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            step = RecoveryStep.Code,
                            email = email,
                            isLoading = false,
                            infoMessage = result.message.ifBlank {
                                "If an account exists for this email, a code has been sent."
                            }
                        )
                    }
                    startResendCooldown()
                }

                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun submitCode(rawCode: String) {
        if (_uiState.value.isLoading) return

        val code = rawCode.trim()
        if (!AuthValidators.isValidCode(code)) {
            _uiState.update {
                it.copy(fieldError = "Enter the ${AuthValidators.OTP_LENGTH}-digit code")
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, fieldError = null, errorMessage = null) }

        viewModelScope.launch {
            when (val result = repository.verifyResetCode(_uiState.value.email, code)) {
                is ApiResult.Success -> {
                    resetToken = result.data
                    _uiState.update {
                        it.copy(step = RecoveryStep.NewPassword, isLoading = false)
                    }
                }

                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun resendCode() {
        val state = _uiState.value
        if (state.isLoading || state.resendSecondsLeft > 0 || state.email.isBlank()) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = repository.requestPasswordReset(state.email)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, infoMessage = "A new code has been sent.")
                    }
                    startResendCooldown()
                }

                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun submitNewPassword(password: String, confirmPassword: String) {
        if (_uiState.value.isLoading) return

        val passwordError = if (AuthValidators.isValidPassword(password)) null
        else "Password must be at least ${AuthValidators.MIN_PASSWORD_LENGTH} characters"
        val confirmError = if (password == confirmPassword) null else "Passwords do not match"

        if (passwordError != null || confirmError != null) {
            _uiState.update { it.copy(fieldError = passwordError, confirmError = confirmError) }
            return
        }

        val token = resetToken
        if (token == null) {
            // Should not happen; send the user back to start cleanly.
            _uiState.update {
                it.copy(
                    step = RecoveryStep.Email,
                    errorMessage = "Your reset session expired. Please start again."
                )
            }
            return
        }

        _uiState.update {
            it.copy(isLoading = true, fieldError = null, confirmError = null, errorMessage = null)
        }

        viewModelScope.launch {
            when (val result = repository.resetPassword(token, password)) {
                is ApiResult.Success -> {
                    resetToken = null
                    cooldownJob?.cancel()
                    _uiState.update {
                        it.copy(
                            step = RecoveryStep.Done,
                            isLoading = false,
                            resendSecondsLeft = 0,
                            infoMessage = result.message.ifBlank { "Password reset successfully." }
                        )
                    }
                }

                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    /**
     * Handles the system/back button. Returns true if the ViewModel consumed it
     * (moved back a step), false if the screen should be closed.
     * Going back from NewPassword restarts the flow because the code is single-use.
     */
    fun onBack(): Boolean {
        val state = _uiState.value
        return when (state.step) {
            RecoveryStep.Code, RecoveryStep.NewPassword -> {
                resetToken = null
                _uiState.update {
                    it.copy(
                        step = RecoveryStep.Email,
                        fieldError = null,
                        confirmError = null,
                        errorMessage = null,
                        infoMessage = null
                    )
                }
                true
            }

            RecoveryStep.Email, RecoveryStep.Done -> false
        }
    }

    fun onInputChanged() {
        _uiState.update { it.copy(fieldError = null, confirmError = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    private fun startResendCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            for (second in RESEND_COOLDOWN_SECONDS downTo 1) {
                _uiState.update { it.copy(resendSecondsLeft = second) }
                delay(1_000.milliseconds)
            }
            _uiState.update { it.copy(resendSecondsLeft = 0) }
        }
    }

    companion object {
        const val RESEND_COOLDOWN_SECONDS = 60
    }
}