package com.clicks.app.feature.auth.service

import android.content.Context
import com.clicks.app.feature.auth.model.Authentication
import com.clicks.app.lib.db.AppDatabase

class AuthenticationLocalSrc(context: Context) {
    private val dao = AppDatabase.getDatabase(context).authenticationDao()

    fun insertSync(auth: Authentication) = dao.insertSync(auth)
    suspend fun insert(auth: Authentication) = dao.insertSync(auth)
    fun selectSync(): Authentication? = dao.selectSync()
    fun delete() = dao.delete()
    fun exists(): Boolean = dao.selectSync() != null
}
