package com.clicks.app.data.remote

import android.content.Context
import com.clicks.app.auth.data.AuthApi
import com.clicks.app.lib.session.TokenStorage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

//    private const val BASE_URL = "http://10.0.2.2:8080/"
    private const val BASE_URL = "https://minishop-backend-mi7h.onrender.com/"



    lateinit var tokenStorage: TokenStorage
        private set

    @Volatile
    private var initialized = false

    @Volatile
    private var authApiInstance: AuthApi? = null

    fun initialize(context: Context) {
        if (initialized) return

        synchronized(this) {
            if (initialized) return

            tokenStorage = TokenStorage(context.applicationContext)

            val client = OkHttpClient.Builder()
                .addInterceptor(
                    AuthInterceptor(
                        tokenProvider = { tokenStorage.getToken() }
                    )
                )
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            authApiInstance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthApi::class.java)

            initialized = true
        }
    }

    val authApi: AuthApi
        get() = checkNotNull(authApiInstance) {
            "ApiClient is not initialized. Call initialize(context) first."
        }
}
