package com.clicks.app.feature.auth.data

import com.clicks.app.feature.auth.data.dto.AuthResponse
import com.clicks.app.feature.auth.data.dto.LoginRequest
import com.clicks.app.feature.auth.data.dto.RegisterRequest
import com.clicks.app.lib.session.TokenStorage
import com.clicks.app.network.ApiClient
import com.clicks.app.network.ApiResult
import com.clicks.app.network.safeApiCall
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val authApi: AuthApi = ApiClient.createService<AuthApi>(),
    private val tokenStorage: TokenStorage = TokenStorage(ApiClient.appContext)
) {

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): ApiResult<AuthResponse> {
        val request = RegisterRequest(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            email = email.trim(),
            password = password
        )
        val result = safeApiCall {
            authApi.register(request)
        }

        return persistSession(result)
    }

    suspend fun login(
        email: String,
        password: String
    ): ApiResult<AuthResponse> {
        val request = LoginRequest(
            email = email.trim(),
            password = password
        )
        val result = safeApiCall {
            authApi.login(request)
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
                message = "An error occurred! Please try again."
            )
        }
    }
}
