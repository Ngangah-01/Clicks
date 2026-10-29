package com.clicks.app.network.dto

data class ApiResponse<T>(
    val success: Boolean? = null,
    val status: Boolean? = null,
    val statusCode: Int? = null,
    val message: String? = null,
    val time: String? = null,
    val data: T? = null
) {
    val isSuccessful: Boolean
        get() = success == true || status == true || (statusCode != null && statusCode in 200..299)
}
