package com.clicks.app.data.remote

import com.clicks.app.data.remote.dto.ApiResponse
import com.clicks.app.data.remote.dto.AuthResponse
import com.clicks.app.data.remote.dto.LoginRequest
import com.clicks.app.data.remote.dto.RegisterRequest
import com.clicks.app.data.remote.dto.UserResponse
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
    suspend fun getCurrentUser():
            ApiResponse<UserResponse>
}
