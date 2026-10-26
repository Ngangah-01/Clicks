package com.clicks.app.auth.data

import com.clicks.app.auth.login.AuthResponse
import com.clicks.app.auth.login.LoginRequest
import com.clicks.app.auth.register.RegisterRequest
import com.clicks.app.auth.register.UserResponse
import com.clicks.app.lib.session.TokenStorage
import com.clicks.app.data.remote.ApiClient
import com.clicks.app.data.remote.ApiResult
import com.clicks.app.data.remote.safeApiCall
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val authApi: AuthApi = ApiClient.authApi,
    private val tokenStorage: TokenStorage = ApiClient.tokenStorage
) {

    suspend fun register(
        request: RegisterRequest
    ): ApiResult<AuthResponse> {
        val result = safeApiCall {
            authApi.register(
                request.copy(
                    firstName = request.firstName.trim(),
                    lastName = request.lastName.trim(),
                    email = request.email.trim()
                )
            )
        }

        return persistSession(result)
    }

    suspend fun login(
        request: LoginRequest
    ): ApiResult<AuthResponse> {
        val result = safeApiCall {
            authApi.login(
                request.copy(email = request.email.trim())
            )
        }

        return persistSession(result)
    }

    suspend fun getCurrentUser(): ApiResult<UserResponse> {
        return safeApiCall {
            authApi.getCurrentUser()
        }
    }

    fun logout() {
        tokenStorage.clearToken()
    }

    private suspend fun persistSession(
        result: ApiResult<AuthResponse>
    ): ApiResult<AuthResponse> {
        if (result !is ApiResult.Success) return result

        return try {
            withContext(Dispatchers.IO) {
                tokenStorage.saveToken(result.data.token)
            }
            result
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            ApiResult.Error(
                message = "An error occurred! Please try again."
            )
        }
    }
}
