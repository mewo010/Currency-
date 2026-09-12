package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.ConversionHistoryEntity
import com.example.data.local.ExchangeRateEntity
import com.example.data.local.FavoritePairEntity
import com.example.data.model.CurrencyData
import com.example.data.network.ExchangeRateApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class RateFetchResult(
    val baseCurrency: String,
    val rates: Map<String, Double>,
    val lastUpdatedText: String,
    val isFromCache: Boolean,
    val errorMessage: String? = null
)

class CurrencyRepository(
    private val apiService: ExchangeRateApiService,
    private val database: AppDatabase
) {
    private val rateDao = database.exchangeRateDao()
    private val favoriteDao = database.favoritePairDao()
    private val historyDao = database.conversionHistoryDao()

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Double::class.javaObjectType)
    private val jsonAdapter = moshi.adapter<Map<String, Double>>(mapType)

    suspend fun getExchangeRates(baseCurrency: String, forceRefresh: Boolean = false): RateFetchResult = withContext(Dispatchers.IO) {
        val baseUpper = baseCurrency.uppercase()

        if (!forceRefresh) {
            // Check local Room cache first
            val cached = rateDao.getRateForBase(baseUpper)
            if (cached != null && (System.currentTimeMillis() - cached.lastUpdatedMillis < 3_600_000)) { // 1 hr cache
                val parsedRates = try {
                    jsonAdapter.fromJson(cached.ratesJson)
                } catch (e: Exception) {
                    null
                }
                if (parsedRates != null && parsedRates.isNotEmpty()) {
                    return@withContext RateFetchResult(
                        baseCurrency = baseUpper,
                        rates = parsedRates,
                        lastUpdatedText = "Cached " + formatTimestamp(cached.lastUpdatedMillis),
                        isFromCache = true
                    )
                }
            }
        }

        // Try API call
        try {
            val response = apiService.getLatestRates(baseUpper)
            val rates = response.rates
            if (response.result == "success" && !rates.isNullOrEmpty()) {
                val jsonRates = jsonAdapter.toJson(rates)
                rateDao.insertRate(
                    ExchangeRateEntity(
                        baseCode = baseUpper,
                        ratesJson = jsonRates,
                        lastUpdatedMillis = System.currentTimeMillis()
                    )
                )
                return@withContext RateFetchResult(
                    baseCurrency = baseUpper,
                    rates = rates,
                    lastUpdatedText = response.timeLastUpdateUtc ?: "Updated just now",
                    isFromCache = false
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // If network failed, try any existing cache regardless of age
        val cachedFallback = rateDao.getRateForBase(baseUpper)
        if (cachedFallback != null) {
            val parsed = try {
                jsonAdapter.fromJson(cachedFallback.ratesJson)
            } catch (e: Exception) {
                null
            }
            if (!parsed.isNullOrEmpty()) {
                return@withContext RateFetchResult(
                    baseCurrency = baseUpper,
                    rates = parsed,
                    lastUpdatedText = "Offline (Cached " + formatTimestamp(cachedFallback.lastUpdatedMillis) + ")",
                    isFromCache = true,
                    errorMessage = "Network unavailable. Showing cached exchange rates."
                )
            }
        }

        // Ultimate fallback to default static rates calculated for base
        val calculatedFallbackRates = calculateFallbackRatesForBase(baseUpper)
        RateFetchResult(
            baseCurrency = baseUpper,
            rates = calculatedFallbackRates,
            lastUpdatedText = "Offline mode (Estimated rates)",
            isFromCache = true,
            errorMessage = "Using offline estimated exchange rates."
        )
    }

    private fun calculateFallbackRatesForBase(base: String): Map<String, Double> {
        val usdRates = CurrencyData.defaultBackupRatesToUsd
        val baseUsdRate = usdRates[base] ?: 1.0
        val result = mutableMapOf<String, Double>()
        usdRates.forEach { (code, usdRate) ->
            result[code] = usdRate / baseUsdRate
        }
        return result
    }

    fun calculateConversion(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
        ratesMap: Map<String, Double>
    ): Double {
        if (fromCurrency.equals(toCurrency, ignoreCase = true)) return amount
        val fromRate = ratesMap[fromCurrency.uppercase()] ?: 1.0
        val toRate = ratesMap[toCurrency.uppercase()] ?: 1.0

        // If rates map is based on 'fromCurrency' (e.g. base = fromCurrency), then toRate is directly the rate!
        if (ratesMap.containsKey("USD")) {
            // Check if fromRate is 1.0 (meaning base was fromCurrency)
            return if (fromRate == 1.0) {
                amount * toRate
            } else {
                (amount / fromRate) * toRate
            }
        }
        return amount
    }

    // Favorites
    fun getFavoritePairs(): Flow<List<FavoritePairEntity>> = favoriteDao.getAllFavoritePairs()

    suspend fun isFavorite(fromCurrency: String, toCurrency: String): Boolean = withContext(Dispatchers.IO) {
        favoriteDao.isFavorite(fromCurrency, toCurrency)
    }

    suspend fun toggleFavorite(fromCurrency: String, toCurrency: String) = withContext(Dispatchers.IO) {
        if (favoriteDao.isFavorite(fromCurrency, toCurrency)) {
            favoriteDao.deleteFavorite(fromCurrency, toCurrency)
        } else {
            favoriteDao.insertFavorite(
                FavoritePairEntity(fromCurrency = fromCurrency, toCurrency = toCurrency)
            )
        }
    }

    // History
    fun getConversionHistory(): Flow<List<ConversionHistoryEntity>> = historyDao.getRecentHistory()

    suspend fun saveHistory(
        fromCurrency: String,
        toCurrency: String,
        fromAmount: Double,
        toAmount: Double,
        rate: Double
    ) = withContext(Dispatchers.IO) {
        if (fromAmount > 0) {
            historyDao.insertHistory(
                ConversionHistoryEntity(
                    fromCurrency = fromCurrency,
                    toCurrency = toCurrency,
                    fromAmount = fromAmount,
                    toAmount = toAmount,
                    rate = rate
                )
            )
        }
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        historyDao.clearAllHistory()
    }

    private fun formatTimestamp(millis: Long): String {
        val diffMinutes = (System.currentTimeMillis() - millis) / 60000
        return when {
            diffMinutes < 1 -> "just now"
            diffMinutes < 60 -> "$diffMinutes mins ago"
            else -> "${diffMinutes / 60} hours ago"
        }
    }
}
