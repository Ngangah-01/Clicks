package com.clicks.app.feature.login.data

import com.clicks.app.feature.register.data.RegisterRequest
import com.clicks.app.feature.register.data.UserResponse
import com.clicks.app.network.dto.ApiResponse
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
    suspend fun getCurrentUser(
    ): ApiResponse<UserResponse>
}
