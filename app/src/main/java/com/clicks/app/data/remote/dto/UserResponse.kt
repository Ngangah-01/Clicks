
package com.clicks.app.data.remote.dto

data class UserResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val createdAt: String?,
    val updatedAt: String?
)
