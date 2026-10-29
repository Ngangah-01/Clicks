package com.clicks.app.network

import com.clicks.app.network.dto.ApiResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SafeApiCallTest {

    private fun httpError(code: Int, body: String) = HttpException(
        Response.error<Any>(code, body.toResponseBody("application/json".toMediaType()))
    )

    @Test
    fun success_withData() = runTest {
        val result = safeApiCall { ApiResponse(true, "ok", "payload") }
        assertEquals(ApiResult.Success("payload", "ok"), result)
    }

    @Test
    fun success_withoutData_isErrorForDataEndpoints() = runTest {
        val result = safeApiCall<String> { ApiResponse(true, "ok", null) }
        assertTrue(result is ApiResult.Error)
    }

    @Test
    fun noData_success_withNullMessage_doesNotCrash() = runTest {
        val result = safeApiCallNoData { ApiResponse<Unit>(true, null, null) }
        assertEquals(ApiResult.Success(Unit, ""), result)
    }

    @Test
    fun noData_successFalse_isServerError() = runTest {
        val result = safeApiCallNoData { ApiResponse<Unit>(false, "Nope", null) }
        assertEquals(ApiResult.Error("Nope", ApiErrorType.Server), result)
    }

    @Test
    fun http400_usesServerMessage() = runTest {
        val result = safeApiCall<String> { throw httpError(400, """{"message":"Invalid code"}""") }
        assertEquals(ApiResult.Error("Invalid code", ApiErrorType.BadRequest, 400), result)
    }

    @Test
    fun http401_nonJsonBody_fallsBackToDefault() = runTest {
        val result = safeApiCall<String> { throw httpError(401, "<html>nope</html>") }
        assertEquals(ApiResult.Error("Invalid email or password.", ApiErrorType.Unauthorized, 401), result)
    }

    @Test
    fun http409_isConflict() = runTest {
        val result = safeApiCall<String> { throw httpError(409, "{}") }
        assertTrue(result is ApiResult.Error && result.type == ApiErrorType.Conflict)
    }

    @Test
    fun http500_isServer() = runTest {
        val result = safeApiCall<String> { throw httpError(500, "") }
        assertTrue(result is ApiResult.Error && result.type == ApiErrorType.Server)
    }

    @Test
    fun ioException_isNetwork() = runTest {
        val result = safeApiCall<String> { throw IOException("boom") }
        assertTrue(result is ApiResult.Error && result.type == ApiErrorType.Network)
    }

    @Test
    fun unexpectedException_isUnknown() = runTest {
        val result = safeApiCallNoData { throw IllegalStateException("boom") }
        assertTrue(result is ApiResult.Error && result.type == ApiErrorType.Unknown)
    }
}
