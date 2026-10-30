package com.clicks.app.feature.auth.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "authentication")
class Authentication {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var expiryTime: Date = Date()
    var authToken: String = ""

    fun hasExpired(): Boolean = this.expiryTime.before(Date())
}
