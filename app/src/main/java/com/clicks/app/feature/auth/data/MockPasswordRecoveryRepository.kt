package com.clicks.app.feature.auth.data

import com.clicks.app.network.ApiErrorType
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.delay

class MockPasswordRecoveryRepository : PasswordRecoveryRepository {

    override suspend fun requestPasswordReset(email: String): ApiResult<Unit> {
        delay(600) // Simulate network latency
        return ApiResult.Success(Unit, "Verification code sent to $email (Mock code: 123456)")
    }

    override suspend fun verifyResetCode(email: String, code: String): ApiResult<String> {
        delay(600)
        return if (code == "123456") {
            ApiResult.Success("mock-reset-token-12345", "Code verified successfully")
        } else {
            ApiResult.Error("Invalid verification code. Use mock code: 123456", ApiErrorType.BadRequest, 400)
        }
    }

    override suspend fun resetPassword(resetToken: String, newPassword: String): ApiResult<Unit> {
        delay(600)
        return ApiResult.Success(Unit, "Password reset successfully. You can now login.")
    }
}
