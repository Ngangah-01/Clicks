package com.clicks.app.data.remote.dto

/**
 * Shape of the JSON body the server returns on 4xx/5xx responses.
 * Add more fields here if your backend sends them (e.g. "errors", "code").
 */
data class ErrorResponse(
    val message: String? = null
)