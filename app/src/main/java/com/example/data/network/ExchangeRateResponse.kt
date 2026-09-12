package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRateResponse(
    @Json(name = "result") val result: String? = null,
    @Json(name = "base_code") val baseCode: String? = null,
    @Json(name = "time_last_update_utc") val timeLastUpdateUtc: String? = null,
    @Json(name = "rates") val rates: Map<String, Double>? = null
)
