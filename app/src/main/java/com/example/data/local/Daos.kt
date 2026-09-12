package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates WHERE baseCode = :baseCode LIMIT 1")
    suspend fun getRateForBase(baseCode: String): ExchangeRateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(entity: ExchangeRateEntity)
}

@Dao
interface FavoritePairDao {
    @Query("SELECT * FROM favorite_pairs ORDER BY addedAtMillis DESC")
    fun getAllFavoritePairs(): Flow<List<FavoritePairEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_pairs WHERE fromCurrency = :fromCurrency AND toCurrency = :toCurrency)")
    suspend fun isFavorite(fromCurrency: String, toCurrency: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(pair: FavoritePairEntity)

    @Query("DELETE FROM favorite_pairs WHERE fromCurrency = :fromCurrency AND toCurrency = :toCurrency")
    suspend fun deleteFavorite(fromCurrency: String, toCurrency: String)
}

@Dao
interface ConversionHistoryDao {
    @Query("SELECT * FROM conversion_history ORDER BY timestampMillis DESC LIMIT 50")
    fun getRecentHistory(): Flow<List<ConversionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: ConversionHistoryEntity)

    @Query("DELETE FROM conversion_history")
    suspend fun clearAllHistory()
}
