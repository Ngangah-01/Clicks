package com.clicks.app.feature.auth.data.dto

data class AuthResponse(
    val token: String,
    val user: UserResponse
)
