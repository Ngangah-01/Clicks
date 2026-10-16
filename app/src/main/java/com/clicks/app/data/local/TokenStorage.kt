package com.clicks.app.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Log

class TokenStorage(context: Context) {

    private val preferences = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Avoids a Keystore decryption on every request.
    @Volatile
    private var cachedToken: String? = null

    fun saveToken(token: String) {
        require(token.isNotBlank()) { "Token cannot be blank" }

        cachedToken = token
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            val encrypted = cipher.doFinal(token.toByteArray(Charsets.UTF_8))

            preferences.edit {
                putString(KEY_TOKEN, encrypted.toBase64())
                putString(KEY_IV, cipher.iv.toBase64())
            }
        } catch (exception: Exception) {
            Log.e("TokenStorage", "Failed to save token", exception)
        }

    }

    fun getToken(): String? {
        cachedToken?.let { return it }

        val encodedToken = preferences.getString(KEY_TOKEN, null) ?: return null
        val encodedIv = preferences.getString(KEY_IV, null) ?: return null

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(
                Cipher.DECRYPT_MODE,
                getExistingKey(),
                GCMParameterSpec(GCM_TAG_BITS, encodedIv.fromBase64())
            )
            cipher.doFinal(encodedToken.fromBase64())
                .toString(Charsets.UTF_8)
                .also { cachedToken = it }
        } catch (exception: Exception) {
            // Key invalidated, data corrupted or restored from backup: no valid session.
            clearToken()
            null
        }
    }

    fun clearToken() {
        cachedToken = null
        preferences.edit {
            remove(KEY_TOKEN)
            remove(KEY_IV)
        }
    }

    private fun getExistingKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        return keyStore.getKey(KEY_ALIAS, null) as? SecretKey
            ?: throw IllegalStateException("Session encryption key is unavailable")
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return keyGenerator.generateKey()
    }

    private fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)
    private fun String.fromBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

    private companion object {
        const val PREFS_NAME = "clicks_secure_session"
        const val KEY_ALIAS = "clicks_session_encryption_key"
        const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_TAG_BITS = 128
        const val KEY_TOKEN = "encrypted_token"
        const val KEY_IV = "token_iv"
    }
}