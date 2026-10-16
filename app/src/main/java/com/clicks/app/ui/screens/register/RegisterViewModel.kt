
package com.clicks.app.ui.screens.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.data.remote.ApiResult
import com.clicks.app.data.remote.dto.RegisterRequest
import com.clicks.app.data.repository.AuthRepository
import kotlinx.coroutines.launch
import android.app.Application
import com.clicks.app.data.repository.SessionRepository

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class RegisterViewModel(
    application: Application,
    private val authRepository: AuthRepository = AuthRepository()
) : AndroidViewModel(application) {

    private val sessionRepository = SessionRepository(application)
    var uiState = mutableStateOf(RegisterUiState())
        private set

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String

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
                    try {
                        sessionRepository.saveToken(result.data.token)

                        uiState.value = RegisterUiState(
                            successMessage = result.message.ifBlank {
                                "Registration succcessful."
                            }
                        )
                    }catch (ex: Exception){
                        uiState.value = RegisterUiState(
                            errorMessage = "Please try again." // session not created
                        )
                    }
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
