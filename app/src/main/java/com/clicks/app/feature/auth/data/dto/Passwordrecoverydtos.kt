package com.clicks.app.feature.auth.data.dto

data class ForgotPasswordRequest(
    val email: String
)

data class VerifyResetCodeRequest(
    val email: String,
    val code: String
)

data class VerifyResetCodeResponse(
    val resetToken: String
)

data class ResetPasswordRequest(
    val resetToken: String,
    val newPassword: String
)