package com.example.data.update

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubRelease(
    @Json(name = "tag_name") val tagName: String = "",
    @Json(name = "name") val name: String? = null,
    @Json(name = "body") val body: String? = null,
    @Json(name = "html_url") val htmlUrl: String = "",
    @Json(name = "published_at") val publishedAt: String? = null,
    @Json(name = "assets") val assets: List<GitHubReleaseAsset> = emptyList()
) {
    fun findApkAsset(): GitHubReleaseAsset? {
        return assets.firstOrNull { it.name.endsWith(".apk", ignoreCase = true) }
    }
}

@JsonClass(generateAdapter = true)
data class GitHubReleaseAsset(
    @Json(name = "name") val name: String = "",
    @Json(name = "size") val size: Long = 0L,
    @Json(name = "browser_download_url") val downloadUrl: String = "",
    @Json(name = "content_type") val contentType: String? = null
)
