package com.clicks.app.feature.auth.data.dto

import com.clicks.app.feature.auth.model.User

data class UserResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val createdAt: String?,
    val updatedAt: String?
)

fun UserResponse.toDomain(): User = User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    role = role
)
