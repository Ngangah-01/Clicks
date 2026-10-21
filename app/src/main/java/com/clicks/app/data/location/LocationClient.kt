package com.clicks.app.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume

sealed interface LocationSettingsStatus {
    /** Device location is on; a fix can be requested. */
    data object Ready : LocationSettingsStatus

    /** Location is off but the user can switch it on from a system dialog. */
    data class NeedsResolution(val pendingIntent: PendingIntent) : LocationSettingsStatus

    /** Could not determine (e.g. Play services problem). Callers may still try. */
    data object Unavailable : LocationSettingsStatus
}

/**
 * Device location (Google's fused provider: free, no API key, no billing) plus
 * reverse geocoding (Android's Geocoder first, OSM Nominatim as a free fallback).
 */
class LocationClient(
    context: Context,
    private val nominatim: NominatimClient = NominatimClient()
) {

    private val appContext = context.applicationContext
    private val fusedClient = LocationServices.getFusedLocationProviderClient(appContext)

    fun hasPreciseAccess(): Boolean = isGranted(Manifest.permission.ACCESS_FINE_LOCATION)

    fun hasAnyAccess(): Boolean =
        isGranted(Manifest.permission.ACCESS_FINE_LOCATION) ||
                isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun isGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(appContext, permission) ==
                PackageManager.PERMISSION_GRANTED

    /** Checks the device-level location switch, so "permission granted but GPS off" is handled. */
    suspend fun checkSettings(): LocationSettingsStatus = suspendCancellableCoroutine { cont ->
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1_000L).build()
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(request)
            .build()

        LocationServices.getSettingsClient(appContext)
            .checkLocationSettings(settingsRequest)
            .addOnSuccessListener {
                if (cont.isActive) cont.resume(LocationSettingsStatus.Ready)
            }
            .addOnFailureListener { error ->
                if (!cont.isActive) return@addOnFailureListener
                cont.resume(
                    if (error is ResolvableApiException) {
                        LocationSettingsStatus.NeedsResolution(error.resolution)
                    } else {
                        LocationSettingsStatus.Unavailable
                    }
                )
            }
    }

    /** Instant but possibly stale or null. Used to show the map near the user straight away. */
    @SuppressLint("MissingPermission")
    suspend fun lastKnown(): Location? {
        if (!hasAnyAccess()) return null
        return suspendCancellableCoroutine { cont ->
            fusedClient.lastLocation
                .addOnSuccessListener { if (cont.isActive) cont.resume(it) }
                .addOnFailureListener { if (cont.isActive) cont.resume(null) }
        }
    }

    /** A fresh, high-accuracy fix. Takes a few seconds; cancelled if the coroutine is cancelled. */
    @SuppressLint("MissingPermission")
    suspend fun currentPrecise(): Location? {
        if (!hasAnyAccess()) return null
        val tokenSource = CancellationTokenSource()
        return suspendCancellableCoroutine { cont ->
            cont.invokeOnCancellation { tokenSource.cancel() }
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, tokenSource.token)
                .addOnSuccessListener { if (cont.isActive) cont.resume(it) }
                .addOnFailureListener { if (cont.isActive) cont.resume(null) }
        }
    }

    /** One-line address for a point, or null if neither geocoder could resolve it. */
    suspend fun addressFor(latitude: Double, longitude: Double): String? =
        platformGeocode(latitude, longitude) ?: nominatim.reverse(latitude, longitude)

    private suspend fun platformGeocode(latitude: Double, longitude: Double): String? {
        if (!Geocoder.isPresent()) return null
        val geocoder = Geocoder(appContext, Locale.getDefault())

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { cont ->
                    geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                if (cont.isActive) cont.resume(addresses.firstOrNull()?.oneLine())
                            }

                            override fun onError(errorMessage: String?) {
                                if (cont.isActive) cont.resume(null)
                            }
                        }
                    )
                }
            } else {
                withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()?.oneLine()
                }
            }
        } catch (_: IOException) {
            null
        }
    }

    private fun Address.oneLine(): String? =
        getAddressLine(0)?.takeIf { it.isNotBlank() }
}