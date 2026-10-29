package com.clicks.app.feature.auth.data

import com.clicks.app.feature.auth.data.dto.AuthResponse
import com.clicks.app.feature.auth.data.dto.ForgotPasswordRequest
import com.clicks.app.feature.auth.data.dto.LoginRequest
import com.clicks.app.feature.auth.data.dto.RegisterRequest
import com.clicks.app.feature.auth.data.dto.ResetPasswordRequest
import com.clicks.app.feature.auth.data.dto.VerifyResetCodeRequest
import com.clicks.app.lib.session.TokenStorage
import com.clicks.app.network.ApiClient
import com.clicks.app.network.ApiResult
import com.clicks.app.network.safeApiCall
import com.clicks.app.network.safeApiCallNoData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val authApi: AuthApi = ApiClient.createService<AuthApi>(),
    private val tokenStorage: TokenStorage = TokenStorage(ApiClient.appContext)
) : PasswordRecoveryRepository {

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

    override suspend fun requestPasswordReset(email: String): ApiResult<Unit> =
        safeApiCallNoData {
            authApi.forgotPassword(ForgotPasswordRequest(email = email.trim()))
        }

    override suspend fun verifyResetCode(
        email: String,
        code: String
    ): ApiResult<String> {
        val result = safeApiCall {
            authApi.verifyResetCode(
                VerifyResetCodeRequest(email = email.trim(), code = code.trim())
            )
        }

        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.resetToken, result.message)
            is ApiResult.Error -> result
        }
    }

    override suspend fun resetPassword(
        resetToken: String,
        newPassword: String
    ): ApiResult<Unit> = safeApiCallNoData {
        authApi.resetPassword(
            ResetPasswordRequest(resetToken = resetToken, newPassword = newPassword)
        )
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