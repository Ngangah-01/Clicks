package com.clicks.app.feature.profile.model

import com.clicks.app.feature.register.data.UserResponse

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String = "USER"
) {
    val displayName: String
        get() = "$firstName $lastName".trim()

    val initials: String
        get() = listOfNotNull(firstName.firstOrNull(), lastName.firstOrNull())
            .joinToString("")
            .uppercase()
}

fun UserResponse.toDomain(): User = User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    role = role
)
