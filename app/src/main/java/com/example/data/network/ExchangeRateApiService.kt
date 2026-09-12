package com.example.data.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApiService {
    @GET("v6/latest/{base}")
    suspend fun getLatestRates(@Path("base") baseCurrency: String): ExchangeRateResponse

    companion object {
        private const val BASE_URL = "https://open.er-api.com/"

        fun create(): ExchangeRateApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(ExchangeRateApiService::class.java)
        }
    }
}
