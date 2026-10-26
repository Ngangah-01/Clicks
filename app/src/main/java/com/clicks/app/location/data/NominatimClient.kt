package com.clicks.app.location.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class NominatimClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    suspend fun reverse(latitude: Double, longitude: Double): String? =
        withContext(Dispatchers.IO) {
            val url = "https://nominatim.openstreetmap.org/reverse".toHttpUrl()
                .newBuilder()
                .addQueryParameter("format", "jsonv2")
                .addQueryParameter("lat", latitude.toString())
                .addQueryParameter("lon", longitude.toString())
                .addQueryParameter("zoom", "18")
                .build()

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use null
                    JSONObject(response.body.string())
                        .optString("display_name")
                        .takeIf { it.isNotBlank() }
                }
            } catch (_: IOException) {
                null
            } catch (_: JSONException) {
                null
            }
        }

    private companion object {
        // Replace the contact with a real email or URL; Nominatim uses it to reach heavy users.
        const val USER_AGENT = "Clicks/1.0 (Android; com.clicks.app; contact: you@example.com)"
    }
}