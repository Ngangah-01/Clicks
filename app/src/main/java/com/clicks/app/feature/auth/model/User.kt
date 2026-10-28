package com.clicks.app.feature.auth.model

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
