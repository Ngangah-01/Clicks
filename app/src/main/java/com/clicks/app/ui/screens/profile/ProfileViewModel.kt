package com.clicks.app.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.data.remote.ApiErrorType
import com.clicks.app.data.remote.ApiResult
import com.clicks.app.data.remote.dto.UserResponse
import com.clicks.app.data.repository.AuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
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

        val hasExistingUser = uiState.user != null
        uiState = uiState.copy(
            isLoading = !hasExistingUser,
            isRefreshing = hasExistingUser,
            errorMessage = null
        )

        loadJob = viewModelScope.launch {
            when (val result = authRepository.getCurrentUser()) {
                is ApiResult.Success -> {
                    uiState = ProfileUiState(
                        isLoading = false,
                        isRefreshing = false,
                        user = result.data,
                        errorMessage = null
                    )
                }

                is ApiResult.Error -> {
                    if (result.isAuthFailure()) {
                        authRepository.logout()
                        uiState = ProfileUiState(isLoading = false, isRefreshing = false, isSessionExpired = true)
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            isRefreshing = false,
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
        type == ApiErrorType.Unauthorized || type == ApiErrorType.Forbidden
}
