package com.clicks.app.auth.login

import com.clicks.app.auth.register.UserResponse

data class AuthResponse(
    val token: String,
    val user: UserResponse
)
