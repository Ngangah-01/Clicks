package com.clicks.app.data.location

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Free reverse geocoding (coordinates -> address) from OpenStreetMap's public Nominatim.
 *
 * Usage policy for the public server: identify your app in the User-Agent, stay at or
 * under 1 request per second, and don't hammer it. The picker ViewModel debounces
 * requests (it only asks after the map stops moving) to stay well within that.
 *
 * IMPORTANT: this class deliberately has its OWN OkHttpClient. The client in ApiClient
 * adds the user's JWT to every non-public request, and that token must never be sent
 * to a third-party server.
 */
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