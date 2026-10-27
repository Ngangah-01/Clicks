package com.clicks.app.feature.login.data

import com.clicks.app.feature.register.data.UserResponse

data class AuthResponse(
    val token: String,
    val user: UserResponse
)
