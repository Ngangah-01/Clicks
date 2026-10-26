package com.clicks.app.auth.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.auth.data.AuthRepository
import com.clicks.app.data.remote.ApiResult
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState = mutableStateOf(LoginUiState())
        private set

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        if (uiState.value.isLoading) return

        uiState.value = LoginUiState(isLoading = true)

        viewModelScope.launch {
            when (
                val result = authRepository.login(
                    LoginRequest(
                        email = email.trim(),
                        password = password
                    )
                )
            ) {
                is ApiResult.Success -> {
                    uiState.value = LoginUiState(
                        successMessage = result.message
                    )
                    onSuccess()
                }

                is ApiResult.Error -> {
                    uiState.value = LoginUiState(
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
