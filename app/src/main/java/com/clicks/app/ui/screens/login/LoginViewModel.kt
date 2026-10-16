package com.clicks.app.ui.screens.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.data.remote.ApiResult
import com.clicks.app.data.remote.dto.LoginRequest
import com.clicks.app.data.repository.AuthRepository
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.clicks.app.data.repository.SessionRepository

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class LoginViewModel(
    application: Application,
    private val authRepository: AuthRepository = AuthRepository()
) : AndroidViewModel(application) {

    private val sessionRepository = SessionRepository(application)

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
                    try {
                        sessionRepository.saveToken(result.data.token)
                        onSuccess()
                        uiState.value = LoginUiState(
                            successMessage = result.message.ifBlank {
                                "Login successful."
                            }
                        )
                    }catch (ex: Exception){
                        uiState.value = LoginUiState(
                            errorMessage = "Please try again." // session not created
                        )
                    }
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
