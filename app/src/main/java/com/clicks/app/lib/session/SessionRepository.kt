package com.clicks.app.lib.session

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
