package com.clicks.app.data.repository

import com.clicks.app.data.local.TokenStorage

class SessionRepository(
    private val tokenStorage: TokenStorage
) {

    fun saveToken(token: String) = tokenStorage.saveToken(token)
    fun getToken(): String? = tokenStorage.getToken()

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun clearSession() = tokenStorage.clearToken()

    fun setOnboardingCompleted(completed: Boolean) = tokenStorage.setOnboardingCompleted(completed)
    fun isOnboardingCompleted(): Boolean = tokenStorage.isOnboardingCompleted()
}
