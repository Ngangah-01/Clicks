package com.clicks.app.auth.data

import com.clicks.app.auth.login.AuthResponse
import com.clicks.app.auth.login.LoginRequest
import com.clicks.app.auth.register.RegisterRequest
import com.clicks.app.auth.register.UserResponse
import com.clicks.app.data.remote.dto.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
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

    @GET("api/auth/me")
    suspend fun getCurrentUser(): ApiResponse<UserResponse>
}
