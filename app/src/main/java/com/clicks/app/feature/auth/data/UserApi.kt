package com.clicks.app.feature.auth.data

import com.clicks.app.feature.auth.data.dto.UserResponse
import com.clicks.app.network.dto.ApiResponse
import retrofit2.http.GET

interface UserApi {

    @GET("api/auth/me")
    suspend fun getCurrentUser(): ApiResponse<UserResponse>
}
