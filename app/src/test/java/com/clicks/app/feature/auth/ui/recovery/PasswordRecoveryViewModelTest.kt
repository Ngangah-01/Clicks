package com.clicks.app.feature.auth.ui.recovery

import com.clicks.app.feature.auth.data.PasswordRecoveryRepository
import com.clicks.app.network.ApiErrorType
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

private class FakeRecoveryRepository : PasswordRecoveryRepository {
    var requestResult: ApiResult<Unit> = ApiResult.Success(Unit)
    var verifyResult: ApiResult<String> = ApiResult.Success("reset-token")
    var resetResult: ApiResult<Unit> = ApiResult.Success(Unit)

    val calls = mutableListOf<String>()

    override suspend fun requestPasswordReset(email: String): ApiResult<Unit> {
        calls += "request:$email"
        return requestResult
    }

    override suspend fun verifyResetCode(email: String, code: String): ApiResult<String> {
        calls += "verify:$email:$code"
        return verifyResult
    }

    override suspend fun resetPassword(resetToken: String, newPassword: String): ApiResult<Unit> {
        calls += "reset:$resetToken:$newPassword"
        return resetResult
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PasswordRecoveryViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: FakeRecoveryRepository
    private lateinit var vm: PasswordRecoveryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repo = FakeRecoveryRepository()
        vm = PasswordRecoveryViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val state get() = vm.uiState.value

    private fun networkError(message: String = "Unable to connect") =
        ApiResult.Error(message, ApiErrorType.Network)

    /** Drives the flow to the NewPassword step. */
    private fun kotlinx.coroutines.test.TestScope.reachNewPasswordStep() {
        vm.submitEmail("john@example.com"); runCurrent()
        vm.submitCode("123456"); runCurrent()
    }

    // ---- Step 1: email ----

    @Test
    fun invalidEmail_showsFieldError_andMakesNoCall() = runTest(dispatcher) {
        vm.submitEmail("not-an-email"); advanceUntilIdle()

        assertEquals("Valid email is required", state.fieldError)
        assertEquals(RecoveryStep.Email, state.step)
        assertEquals(emptyList<String>(), repo.calls)
    }

    @Test
    fun validEmail_movesToCodeStep_andStartsCooldown() = runTest(dispatcher) {
        vm.submitEmail("  john@example.com "); runCurrent()

        assertEquals(RecoveryStep.Code, state.step)
        assertEquals("john@example.com", state.email)
        assertEquals(PasswordRecoveryViewModel.RESEND_COOLDOWN_SECONDS, state.resendSecondsLeft)
        assertEquals(listOf("request:john@example.com"), repo.calls)
    }

    @Test
    fun emailRequestFailure_staysOnEmail_withErrorMessage() = runTest(dispatcher) {
        repo.requestResult = networkError()
        vm.submitEmail("john@example.com"); runCurrent()

        assertEquals(RecoveryStep.Email, state.step)
        assertEquals("Unable to connect", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun doubleSubmit_whileLoading_callsRepositoryOnce() = runTest(dispatcher) {
        vm.submitEmail("john@example.com")
        vm.submitEmail("john@example.com")
        runCurrent()

        assertEquals(1, repo.calls.count { it.startsWith("request:") })
    }

    // ---- Step 2: code ----

    @Test
    fun invalidCode_showsFieldError_andMakesNoVerifyCall() = runTest(dispatcher) {
        vm.submitEmail("john@example.com"); runCurrent()
        vm.submitCode("12345"); runCurrent()

        assertNotNull(state.fieldError)
        assertEquals(RecoveryStep.Code, state.step)
        assertEquals(0, repo.calls.count { it.startsWith("verify:") })
    }

    @Test
    fun wrongCode_staysOnCodeStep_withServerMessage() = runTest(dispatcher) {
        repo.verifyResult = ApiResult.Error("Invalid or expired code", ApiErrorType.BadRequest, 400)
        vm.submitEmail("john@example.com"); runCurrent()
        vm.submitCode("000000"); runCurrent()

        assertEquals(RecoveryStep.Code, state.step)
        assertEquals("Invalid or expired code", state.errorMessage)
    }

    @Test
    fun correctCode_movesToNewPasswordStep() = runTest(dispatcher) {
        reachNewPasswordStep()
        assertEquals(RecoveryStep.NewPassword, state.step)
    }

    // ---- Resend ----

    @Test
    fun resend_isBlockedDuringCooldown_andAllowedAfter() = runTest(dispatcher) {
        vm.submitEmail("john@example.com"); runCurrent()

        vm.resendCode(); runCurrent()
        assertEquals(1, repo.calls.count { it.startsWith("request:") })

        advanceTimeBy(PasswordRecoveryViewModel.RESEND_COOLDOWN_SECONDS * 1_000L); runCurrent()
        assertEquals(0, state.resendSecondsLeft)

        vm.resendCode(); runCurrent()
        assertEquals(2, repo.calls.count { it.startsWith("request:") })
        assertEquals(PasswordRecoveryViewModel.RESEND_COOLDOWN_SECONDS, state.resendSecondsLeft)
    }

    // ---- Step 3: new password ----

    @Test
    fun shortPassword_isRejected() = runTest(dispatcher) {
        reachNewPasswordStep()
        vm.submitNewPassword("12345", "12345"); runCurrent()

        assertNotNull(state.fieldError)
        assertEquals(0, repo.calls.count { it.startsWith("reset:") })
    }

    @Test
    fun mismatchedPasswords_areRejected() = runTest(dispatcher) {
        reachNewPasswordStep()
        vm.submitNewPassword("secret123", "secret124"); runCurrent()

        assertEquals("Passwords do not match", state.confirmError)
        assertEquals(0, repo.calls.count { it.startsWith("reset:") })
    }

    @Test
    fun successfulReset_usesVerifiedToken_andFinishes() = runTest(dispatcher) {
        reachNewPasswordStep()
        vm.submitNewPassword("secret123", "secret123"); runCurrent()

        assertEquals(RecoveryStep.Done, state.step)
        assertEquals("reset:reset-token:secret123", repo.calls.last())
        assertEquals(0, state.resendSecondsLeft)
    }

    @Test
    fun resetFailure_staysOnNewPasswordStep() = runTest(dispatcher) {
        repo.resetResult = ApiResult.Error("Reset token expired", ApiErrorType.BadRequest, 400)
        reachNewPasswordStep()
        vm.submitNewPassword("secret123", "secret123"); runCurrent()

        assertEquals(RecoveryStep.NewPassword, state.step)
        assertEquals("Reset token expired", state.errorMessage)
        assertFalse(state.isLoading)
    }

    // ---- Back handling ----

    @Test
    fun back_fromEmailStep_isNotConsumed() = runTest(dispatcher) {
        assertFalse(vm.onBack())
    }

    @Test
    fun back_fromNewPassword_restartsFlow_andDropsToken() = runTest(dispatcher) {
        reachNewPasswordStep()

        assertEquals(true, vm.onBack())
        assertEquals(RecoveryStep.Email, state.step)

        // Token must be gone: submitting a password directly must not call the API.
        vm.submitNewPassword("secret123", "secret123"); runCurrent()
        assertEquals(0, repo.calls.count { it.startsWith("reset:") })
    }
}
