package com.clicks.app.auth.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.auth.data.AuthRepository
import com.clicks.app.data.remote.ApiResult
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState = mutableStateOf(RegisterUiState())
        private set

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        if (uiState.value.isLoading) return

        uiState.value = RegisterUiState(isLoading = true)

        viewModelScope.launch {
            when (
                val result = authRepository.register(
                    RegisterRequest(
                        firstName = firstName.trim(),
                        lastName = lastName.trim(),
                        email = email.trim(),
                        password = password
                    )
                )
            ) {
                is ApiResult.Success -> {
                    uiState.value = RegisterUiState(
                        successMessage = result.message.ifBlank {
                            "Registration successful."
                        }
                    )
                    onSuccess()
                }

                is ApiResult.Error -> {
                    uiState.value = RegisterUiState(
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearMessages() {
        uiState.value = uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}
