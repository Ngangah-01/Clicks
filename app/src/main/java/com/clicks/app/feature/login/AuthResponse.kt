package com.clicks.app.feature.login

import com.clicks.app.feature.register.data.UserResponse

data class AuthResponse(
    val token: String,
    val user: UserResponse
)
