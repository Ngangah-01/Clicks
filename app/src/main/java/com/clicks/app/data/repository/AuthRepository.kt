package com.clicks.app.data.repository

import com.clicks.app.data.remote.ApiClient
import com.clicks.app.data.remote.ApiResult
import com.clicks.app.data.remote.safeApiCall
import com.clicks.app.data.remote.AuthApi
import com.clicks.app.data.remote.dto.AuthResponse
import com.clicks.app.data.remote.dto.LoginRequest
import com.clicks.app.data.remote.dto.RegisterRequest

class AuthRepository(
    private val authApi: AuthApi = ApiClient.authApi
) {

    suspend fun register(request: RegisterRequest): ApiResult<AuthResponse> =
        safeApiCall {
            authApi.register(
                request.copy(
                    firstName = request.firstName.trim(),
                    lastName = request.lastName.trim(),
                    email = request.email.trim()
                )
            )
        }

    suspend fun login(request: LoginRequest): ApiResult<AuthResponse> =
        safeApiCall {
            authApi.login(
                request.copy(email = request.email.trim())
            )
        }
}