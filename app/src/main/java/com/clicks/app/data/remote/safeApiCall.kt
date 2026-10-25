package com.clicks.app.data.remote

import android.util.Log
import com.clicks.app.data.remote.dto.ApiResponse
import com.clicks.app.data.remote.dto.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "SafeApiCall"

// One shared Gson instance (creating Gson is relatively expensive).
private val errorGson = Gson()

/**
 * Runs a Retrofit call that returns [ApiResponse] and converts every outcome
 * (success, server-side failure, HTTP error, no connection, unexpected crash)
 * into an [ApiResult]. Use it from any repository.
 */
suspend fun <T> safeApiCall(
    call: suspend () -> ApiResponse<T>
): ApiResult<T> {
    return try {
        val response = call()
        val data = response.data

        if (response.success && data != null) {
            ApiResult.Success(
                data = data,
                message = response.message
            )
        } else {
            ApiResult.Error(
                message = response.message.ifBlank {
                    "The request could not be completed."
                },
                type = ApiErrorType.Server
            )
        }
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: HttpException) {
        exception.toApiError()
    } catch (exception: IOException) {
        Log.e(TAG, "Network error", exception)
        ApiResult.Error(
            message = "Unable to connect to the server. Check your connection and try again.",
            type = ApiErrorType.Network
        )
    } catch (exception: Exception) {
        Log.e(TAG, "Unexpected error during API call", exception)
        ApiResult.Error(
            message = "Something went wrong. Please try again.",
            type = ApiErrorType.Unknown
        )
    }
}

private fun HttpException.toApiError(): ApiResult.Error {
    val code = code()

    val serverMessage = runCatching {
        val body = response()?.errorBody()?.string()
        errorGson.fromJson(body, ErrorResponse::class.java)?.message
    }.getOrNull()

    val type = when (code) {
        400 -> ApiErrorType.BadRequest
        401 -> ApiErrorType.Unauthorized
        403 -> ApiErrorType.Forbidden
        404 -> ApiErrorType.NotFound
        409 -> ApiErrorType.Conflict
        in 500..599 -> ApiErrorType.Server
        else -> ApiErrorType.Unknown
    }

    val message = if (!serverMessage.isNullOrBlank()) {
        serverMessage
    } else {
        when (code) {
            400 -> "Please check the information you entered."
            401 -> "Invalid email or password."
            403 -> "You are not authorized to perform this action."
            404 -> "The requested resource was not found."
            409 -> "An account with these details may already exist."
            in 500..599 -> "A server error occurred. Please try again later."
            else -> "Request failed ($code). Please try again."
        }
    }

    return ApiResult.Error(
        message = message,
        type = type,
        code = code
    )
}