package com.example.data.model

data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
    val flagEmoji: String,
    val countryCode: String
)

data class CurrencyConversion(
    val fromCode: String,
    val toCode: String,
    val amount: Double,
    val result: Double,
    val rate: Double,
    val timestamp: Long = System.currentTimeMillis()
)
