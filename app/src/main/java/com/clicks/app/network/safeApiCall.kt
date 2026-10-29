package com.clicks.app.network

import android.util.Log
import com.clicks.app.network.dto.ApiResponse
import com.clicks.app.network.dto.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "SafeApiCall"

// One shared Gson instance (creating Gson is relatively expensive).
private val errorGson = Gson()

/**
 * For endpoints that must return data (login, register, verify code...).
 * A response with success=true but no data is treated as an error.
 */
suspend fun <T> safeApiCall(
    call: suspend () -> ApiResponse<T>
): ApiResult<T> = guardedCall {
    val response = call()
    val data = response.data

    if (response.isSuccessful && data != null) {
        ApiResult.Success(data = data, message = response.message.orEmpty())
    } else {
        ApiResult.Error(
            message = response.message.orEmpty().ifBlank {
                "The request could not be completed."
            },
            type = ApiErrorType.Server
        )
    }
}

/**
 * For endpoints where success is just success=true and there is no payload
 * (forgot-password, reset-password, logout...).
 */
suspend fun safeApiCallNoData(
    call: suspend () -> ApiResponse<*>
): ApiResult<Unit> = guardedCall {
    val response = call()

    if (response.isSuccessful) {
        ApiResult.Success(data = Unit, message = response.message.orEmpty())
    } else {
        ApiResult.Error(
            message = response.message.orEmpty().ifBlank {
                "The request could not be completed."
            },
            type = ApiErrorType.Server
        )
    }
}

private suspend fun <T> guardedCall(
    block: suspend () -> ApiResult<T>
): ApiResult<T> {
    return try {
        block()
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

    return ApiResult.Error(message = message, type = type, code = code)
}
