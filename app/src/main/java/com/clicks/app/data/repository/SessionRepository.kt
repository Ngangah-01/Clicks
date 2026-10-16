package com.clicks.app.data.repository

import android.content.Context
import com.clicks.app.data.local.TokenStorage

class SessionRepository(context: Context) {

    private val tokenStorage = TokenStorage(context)

    fun saveToken(token: String) {
        tokenStorage.saveToken(token)
    }

    fun getToken(): String? {
        return tokenStorage.getToken()
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }

    fun clearSession() {
        tokenStorage.clearToken()
    }
}