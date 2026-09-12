package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey val baseCode: String,
    val ratesJson: String,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_pairs")
data class FavoritePairEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromCurrency: String,
    val toCurrency: String,
    val addedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversion_history")
data class ConversionHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromCurrency: String,
    val toCurrency: String,
    val fromAmount: Double,
    val toAmount: Double,
    val rate: Double,
    val timestampMillis: Long = System.currentTimeMillis()
)
