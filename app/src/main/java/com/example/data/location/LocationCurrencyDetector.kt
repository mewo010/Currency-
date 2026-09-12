package com.example.data.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.telephony.TelephonyManager
import com.example.data.model.Currency
import com.example.data.model.CurrencyData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

data class LocationDetectionResult(
    val currency: Currency,
    val countryName: String,
    val countryCode: String,
    val isFallback: Boolean = false,
    val message: String
)

class LocationCurrencyDetector(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    suspend fun detectLocalCurrency(): LocationDetectionResult {
        try {
            val location = getLastKnownLocation()
            if (location != null) {
                val countryCode = getCountryCodeFromCoordinates(location.latitude, location.longitude)
                if (!countryCode.isNullOrBlank()) {
                    val currency = CurrencyData.getCurrencyForCountry(countryCode)
                    val countryDisplayName = Locale("", countryCode).displayCountry.ifBlank { countryCode }
                    return LocationDetectionResult(
                        currency = currency,
                        countryName = countryDisplayName,
                        countryCode = countryCode,
                        isFallback = false,
                        message = "Detected location in $countryDisplayName! Set currency to ${currency.name} (${currency.code})."
                    )
                }
            }
        } catch (e: SecurityException) {
            // Permission missing
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fallback to Telephony or Device Locale if GPS location was unavailable
        return getLocaleFallbackCurrency()
    }

    @Suppress("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { continuation ->
        try {
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    // Try last location
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        continuation.resume(lastLoc)
                    }.addOnFailureListener {
                        continuation.resume(null)
                    }
                }
            }.addOnFailureListener {
                continuation.resume(null)
            }
        } catch (e: Exception) {
            continuation.resume(null)
        }
    }

    private fun getCountryCodeFromCoordinates(lat: Double, lng: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var countryCode: String? = null
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    countryCode = addresses.firstOrNull()?.countryCode
                }
                countryCode
            } else {
                @Suppress("DEPRECATION")
                val addresses: List<Address>? = geocoder.getFromLocation(lat, lng, 1)
                addresses?.firstOrNull()?.countryCode
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getLocaleFallbackCurrency(): LocationDetectionResult {
        var countryCode: String? = null
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            countryCode = telephonyManager?.networkCountryIso?.uppercase()
            if (countryCode.isNullOrBlank()) {
                countryCode = telephonyManager?.simCountryIso?.uppercase()
            }
        } catch (e: Exception) {
            // Ignore
        }

        if (countryCode.isNullOrBlank()) {
            val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales.get(0)
            } else {
                @Suppress("DEPRECATION")
                context.resources.configuration.locale
            }
            countryCode = locale.country
        }

        val currency = CurrencyData.getCurrencyForCountry(countryCode)
        val countryDisplayName = if (!countryCode.isNullOrBlank()) {
            Locale("", countryCode).displayCountry.ifBlank { countryCode }
        } else {
            "your region"
        }

        return LocationDetectionResult(
            currency = currency,
            countryName = countryDisplayName,
            countryCode = countryCode ?: "US",
            isFallback = true,
            message = "Set currency to ${currency.name} (${currency.code}) based on $countryDisplayName."
        )
    }
}
