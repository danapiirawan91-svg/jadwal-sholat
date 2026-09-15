package com.example.data.prayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object LocationHelper {

    private const val PREFS_NAME = "panduan_islam_prefs"
    private const val KEY_LAT = "user_lat"
    private const val KEY_LNG = "user_lng"
    private const val KEY_CITY_NAME = "user_city_name"
    private const val KEY_COUNTRY = "user_country"
    private const val KEY_TZ = "user_tz"
    private const val KEY_IS_GPS = "user_is_gps"

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    fun saveUserLocation(context: Context, city: CityLocation, isGps: Boolean = true) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putFloat(KEY_LAT, city.latitude.toFloat())
            .putFloat(KEY_LNG, city.longitude.toFloat())
            .putString(KEY_CITY_NAME, city.name)
            .putString(KEY_COUNTRY, city.country)
            .putFloat(KEY_TZ, city.timezoneOffsetHours.toFloat())
            .putBoolean(KEY_IS_GPS, isGps)
            .apply()
    }

    fun getSavedUserLocation(context: Context): Pair<CityLocation, Boolean>? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_LAT) || !prefs.contains(KEY_LNG)) {
            return null
        }
        val lat = prefs.getFloat(KEY_LAT, 0f).toDouble()
        val lng = prefs.getFloat(KEY_LNG, 0f).toDouble()
        val name = prefs.getString(KEY_CITY_NAME, "Lokasi Saya") ?: "Lokasi Saya"
        val country = prefs.getString(KEY_COUNTRY, "Indonesia") ?: "Indonesia"
        val tz = prefs.getFloat(KEY_TZ, 7f).toDouble()
        val isGps = prefs.getBoolean(KEY_IS_GPS, false)

        return Pair(
            CityLocation(
                name = name,
                country = country,
                latitude = lat,
                longitude = lng,
                timezoneOffsetHours = tz,
                isPopular = true
            ),
            isGps
        )
    }

    @SuppressLint("MissingPermission")
    suspend fun detectCurrentLocation(context: Context): CityLocation? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context)) return@withContext null

        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        var location: Location? = null

        // 1. Try fresh high accuracy location with CancellationToken
        try {
            val cts = CancellationTokenSource()
            val task = fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            location = Tasks.await(task, 4, TimeUnit.SECONDS)
        } catch (e: Throwable) {
            // fallback
        }

        // 2. Fallback to lastLocation if fresh location is null
        if (location == null) {
            try {
                val lastTask = fusedClient.lastLocation
                location = Tasks.await(lastTask, 2, TimeUnit.SECONDS)
            } catch (e: Throwable) {
                // fallback
            }
        }

        // 3. Fallback to Android system LocationManager (Network/GPS)
        if (location == null) {
            try {
                val locManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                if (locManager != null) {
                    val netLoc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    val gpsLoc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    location = gpsLoc ?: netLoc
                }
            } catch (e: Throwable) {
                // ignore
            }
        }

        if (location == null) return@withContext null

        val lat = location.latitude
        val lng = location.longitude
        val tzOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000.0

        var cityName = "Lokasi Saya"
        var countryOrRegion = "Otomatis (GPS)"

        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val list = geocoder.getFromLocation(lat, lng, 1)
            if (!list.isNullOrEmpty()) {
                val addr = list[0]
                val loc = addr.locality // e.g. "Bandung", "Surabaya", "Gambir"
                val subAdmin = addr.subAdminArea // e.g. "Kota Bandung", "Kab. Sleman", "Jakarta Pusat"
                val admin = addr.adminArea // e.g. "Jawa Barat", "DKI Jakarta"
                val country = addr.countryName // e.g. "Indonesia"

                cityName = loc ?: subAdmin ?: admin ?: "Daerah Anda"
                countryOrRegion = when {
                    admin != null && admin != cityName -> "$admin, ${country ?: "Indonesia"}"
                    subAdmin != null && subAdmin != cityName -> subAdmin
                    else -> country ?: "Indonesia"
                }
            }
        } catch (e: Throwable) {
            cityName = "Koordinat (${String.format(Locale.US, "%.2f, %.2f", lat, lng)})"
            countryOrRegion = "Deteksi GPS"
        }

        val detectedCity = CityLocation(
            name = cityName,
            country = countryOrRegion,
            latitude = lat,
            longitude = lng,
            timezoneOffsetHours = tzOffset,
            isPopular = true
        )

        // Cache into preferences
        saveUserLocation(context, detectedCity, isGps = true)

        detectedCity
    }
}
