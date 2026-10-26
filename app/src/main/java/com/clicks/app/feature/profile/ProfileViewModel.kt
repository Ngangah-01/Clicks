package com.clicks.app.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.feature.login.data.AuthRepository
import com.clicks.app.feature.register.data.UserResponse
import com.clicks.app.network.ApiErrorType
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: UserResponse? = null,
    val errorMessage: String? = null,
    val isSessionExpired: Boolean = false
)

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    private var loadJob: Job? = null

    init {
        loadProfile()
    }

    fun loadProfile() {
        if (loadJob?.isActive == true) return

        // Keep any user already on screen so a failed refresh doesn't blank it.
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        loadJob = viewModelScope.launch {
            when (val result = authRepository.getCurrentUser()) {
                is ApiResult.Success -> {
                    uiState = ProfileUiState(isLoading = false, user = result.data)
                }

                is ApiResult.Error -> {
                    if (result.isAuthFailure()) {
                        authRepository.logout()
                        uiState = ProfileUiState(isLoading = false, isSessionExpired = true)
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    private fun ApiResult.Error.isAuthFailure() =
        type == ApiErrorType.Unauthorized ||
        type == ApiErrorType.Forbidden ||
        type == ApiErrorType.NotFound ||
        code == 401 || code == 403 || code == 404
}