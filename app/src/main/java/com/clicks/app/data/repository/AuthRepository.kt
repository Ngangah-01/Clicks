
package com.clicks.app.data.repository

import com.clicks.app.data.local.TokenStorage
import com.clicks.app.data.remote.ApiClient
import com.clicks.app.data.remote.ApiResult
import com.clicks.app.data.remote.AuthApi
import com.clicks.app.data.remote.safeApiCall
import com.clicks.app.data.remote.dto.AuthResponse
import com.clicks.app.data.remote.dto.LoginRequest
import com.clicks.app.data.remote.dto.RegisterRequest
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
                message = "Authentication succeeded, but your session could not be saved. Please try signing in again."
            )
        }
    }
}
