package com.clicks.app.data.remote

/**
 * Categories of failure so the UI can react differently
 * (e.g. highlight the email field on [Conflict], show a retry on [Network]).
 */
enum class ApiErrorType {
    Network,
    BadRequest,
    Unauthorized,
    Forbidden,
    NotFound,
    Conflict,
    Server,
    Unknown
}

sealed interface ApiResult<out T> {

    data class Success<T>(
        val data: T,
        val message: String = ""
    ) : ApiResult<T>

    data class Error(
        val message: String,
        val type: ApiErrorType = ApiErrorType.Unknown,
        val code: Int? = null
    ) : ApiResult<Nothing>
}

/** Convenience helpers for ViewModels. */
inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (ApiResult.Error) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(this)
    return this
}