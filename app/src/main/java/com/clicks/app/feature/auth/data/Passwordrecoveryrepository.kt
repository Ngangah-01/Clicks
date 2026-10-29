package com.clicks.app.feature.auth.data

import com.clicks.app.network.ApiResult

/** Interface so the ViewModel can be unit tested with a fake. */
interface PasswordRecoveryRepository {

    /** Sends a one-time code to the email. Server should respond the same whether or not the account exists. */
    suspend fun requestPasswordReset(email: String): ApiResult<Unit>

    /** Verifies the code and returns a short-lived reset token. */
    suspend fun verifyResetCode(email: String, code: String): ApiResult<String>

    suspend fun resetPassword(resetToken: String, newPassword: String): ApiResult<Unit>
}