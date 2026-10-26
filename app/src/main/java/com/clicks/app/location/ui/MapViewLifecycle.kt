package com.clicks.app.location.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.MapLibre
import org.maplibre.android.maps.MapView

/**
 * OpenFreeMap "Liberty" style: free, no API key, no registration, no request limits.
 * It is a community-run public service provided as-is, so treat it as best-effort.
 * Attribution to OpenStreetMap and OpenMapTiles is shown by the map's built-in (i) button.
 */
const val MAP_STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"

/**
 * MapLibre's MapView is a classic Android View that must be told about the host's
 * lifecycle. This creates one and forwards the lifecycle events for the time the
 * composable is on screen, then cleans up when it leaves.
 */
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapLibre.getInstance(context.applicationContext)
        MapView(context)
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle, mapView) {
        var started = false
        var resumed = false

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> {
                    mapView.onStart()
                    started = true
                }
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                    resumed = true
                }
                Lifecycle.Event.ON_PAUSE -> if (resumed) {
                    mapView.onPause()
                    resumed = false
                }
                Lifecycle.Event.ON_STOP -> if (started) {
                    mapView.onStop()
                    started = false
                }
                else -> Unit
            }
        }

        // Adding the observer replays events up to the current state (CREATE, START, RESUME).
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            // The screen can leave while the Activity is still resumed; wind the view down by hand.
            if (resumed) mapView.onPause()
            if (started) mapView.onStop()
            mapView.onDestroy()
        }
    }

    return mapView
}