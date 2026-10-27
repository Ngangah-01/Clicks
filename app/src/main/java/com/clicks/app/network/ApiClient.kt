package com.clicks.app.network

import android.content.Context
import com.clicks.app.lib.session.TokenStorage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "https://minishop-backend-mi7h.onrender.com/"

    lateinit var tokenStorage: TokenStorage
        private set

    @Volatile
    private var initialized = false

    @Volatile
    private var retrofitInstance: Retrofit? = null

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

            retrofitInstance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            initialized = true
        }
    }

    val retrofit: Retrofit
        get() = checkNotNull(retrofitInstance) {
            "ApiClient is not initialized. Call initialize(context) first."
        }

    fun <T> createService(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> createService(): T = createService(T::class.java)
}
