package com.example.data.update

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface GitHubReleaseService {

    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("User-Agent") userAgent: String = "GlobalCash-Android-App",
        @Header("Accept") accept: String = "application/vnd.github.v3+json"
    ): GitHubRelease

    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create(okHttpClient: OkHttpClient? = null): GitHubReleaseService {
            val client = okHttpClient ?: OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(GitHubReleaseService::class.java)
        }
    }
}
