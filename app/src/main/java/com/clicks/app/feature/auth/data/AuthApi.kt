package com.clicks.app.feature.auth.data

import com.clicks.app.feature.auth.data.dto.AuthResponse
import com.clicks.app.feature.auth.data.dto.LoginRequest
import com.clicks.app.feature.auth.data.dto.RegisterRequest
import com.clicks.app.feature.auth.data.dto.ForgotPasswordRequest
import com.clicks.app.feature.auth.data.dto.ResetPasswordRequest
import com.clicks.app.feature.auth.data.dto.VerifyResetCodeRequest
import com.clicks.app.feature.auth.data.dto.VerifyResetCodeResponse
import com.clicks.app.network.dto.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): ApiResponse<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<AuthResponse>

//    password recovery

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): ApiResponse<Unit>

    @POST("api/auth/verify-reset-code")
    suspend fun verifyResetCode(
        @Body request: VerifyResetCodeRequest
    ): ApiResponse<VerifyResetCodeResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): ApiResponse<Unit>

}
