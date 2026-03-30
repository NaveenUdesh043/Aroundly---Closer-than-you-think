package com.aroundly.aroundly.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import android.location.Geocoder.isPresent

data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val cityHint: String? = null
)

class LocationProvider(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationResult? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission()) return@withContext null

        try {
            val cancellationToken = CancellationTokenSource().token

            val location = try {
                fusedLocationClient
                    .getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationToken
                    )
                    .await()
            } catch (e: SecurityException) {
                return@withContext null
            }

            val safeLocation = location ?: fusedLocationClient.lastLocation.await()

            safeLocation?.let {
                LocationResult(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    cityHint = getCityFromLocation(it.latitude, it.longitude)
                )
            }
        } catch (e: Exception) {
            null
        }
    }


    private fun getCityFromLocation(lat: Double, lng: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            addresses?.firstOrNull()?.locality
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Resolves an address string to coordinates (Google Play services / backend geocoder).
     * May return null if offline, ambiguous address, or Geocoder unavailable (some emulators).
     */
    @Suppress("DEPRECATION")
    suspend fun geocodeAddressToCoordinates(address: String): Pair<Double, Double>? =
        withContext(Dispatchers.IO) {
            val trimmed = address.trim()
            if (trimmed.isEmpty()) return@withContext null
            try {
                if (!Geocoder.isPresent()) return@withContext null
                val geocoder = Geocoder(context, Locale.getDefault())
                val list = geocoder.getFromLocationName(trimmed, 5) ?: return@withContext null
                val a = list.firstOrNull() ?: return@withContext null
                Pair(a.latitude, a.longitude)
            } catch (_: Exception) {
                null
            }
        }

    /**
     * Distance from user's current location to the place: prefers geocoding [placeAddress],
     * then falls back to stored [placeLat]/[placeLng]. Returns null if location unavailable.
     */
    suspend fun distanceFromUserToPlaceKm(
        placeAddress: String?,
        placeLat: Double?,
        placeLng: Double?
    ): Double? {
        val user = getCurrentLocation() ?: return null
        val addr = placeAddress?.trim().orEmpty()
        if (addr.isNotEmpty()) {
            val geo = geocodeAddressToCoordinates(addr)
            if (geo != null) {
                return DistanceUtils.haversineKm(
                    user.latitude,
                    user.longitude,
                    geo.first,
                    geo.second
                )
            }
        }
        if (placeLat != null && placeLng != null) {
            return DistanceUtils.haversineKm(
                user.latitude,
                user.longitude,
                placeLat,
                placeLng
            )
        }
        return null
    }
}
