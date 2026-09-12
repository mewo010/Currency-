package com.example.data.update

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed class UpdateCheckResult {
    data class UpdateAvailable(
        val currentVersion: String,
        val latestRelease: GitHubRelease,
        val apkAsset: GitHubReleaseAsset
    ) : UpdateCheckResult()

    data class UpToDate(
        val currentVersion: String,
        val latestVersion: String
    ) : UpdateCheckResult()

    data class NoApkFound(
        val currentVersion: String,
        val latestRelease: GitHubRelease
    ) : UpdateCheckResult()

    data class Error(val message: String) : UpdateCheckResult()
}

class AppUpdateManager(
    private val context: Context,
    private val gitHubService: GitHubReleaseService = GitHubReleaseService.create(),
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "global_cash_update_prefs"
        private const val KEY_REPO_OWNER = "repo_owner"
        private const val KEY_REPO_NAME = "repo_name"
        const val DEFAULT_REPO_OWNER = "mewo010"
        const val DEFAULT_REPO_NAME = "Currency-"
    }

    fun getStoredRepository(): Pair<String, String> {
        val storedOwner = prefs.getString(KEY_REPO_OWNER, null)
        // Auto-migrate old default omriyosi to new owner mewo010
        val owner = if (storedOwner == null || storedOwner.equals("omriyosi", ignoreCase = true)) {
            if (storedOwner != null && storedOwner.equals("omriyosi", ignoreCase = true)) {
                prefs.edit().putString(KEY_REPO_OWNER, DEFAULT_REPO_OWNER).apply()
            }
            DEFAULT_REPO_OWNER
        } else {
            storedOwner
        }
        val repo = prefs.getString(KEY_REPO_NAME, null) ?: DEFAULT_REPO_NAME
        return Pair(owner, repo)
    }

    fun saveRepository(owner: String, repo: String) {
        prefs.edit()
            .putString(KEY_REPO_OWNER, owner.trim())
            .putString(KEY_REPO_NAME, repo.trim())
            .apply()
    }

    fun getCurrentVersionName(): String {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            packageInfo.versionName ?: BuildConfig.VERSION_NAME
        } catch (_: Exception) {
            BuildConfig.VERSION_NAME
        }
    }

    suspend fun checkForUpdate(
        owner: String? = null,
        repo: String? = null
    ): UpdateCheckResult = withContext(Dispatchers.IO) {
        val (currentOwner, currentRepo) = if (owner != null && repo != null) {
            Pair(owner, repo)
        } else {
            getStoredRepository()
        }

        try {
            val release = gitHubService.getLatestRelease(currentOwner, currentRepo)
            val currentVer = getCurrentVersionName()
            val remoteVer = release.tagName

            val isNewer = isNewerVersion(currentVer, remoteVer)

            if (!isNewer) {
                return@withContext UpdateCheckResult.UpToDate(
                    currentVersion = currentVer,
                    latestVersion = remoteVer
                )
            }

            val apkAsset = release.findApkAsset()
            if (apkAsset != null) {
                UpdateCheckResult.UpdateAvailable(
                    currentVersion = currentVer,
                    latestRelease = release,
                    apkAsset = apkAsset
                )
            } else {
                UpdateCheckResult.NoApkFound(
                    currentVersion = currentVer,
                    latestRelease = release
                )
            }
        } catch (e: Exception) {
            UpdateCheckResult.Error(
                e.localizedMessage ?: "Failed to check for updates from GitHub"
            )
        }
    }

    suspend fun downloadApk(
        downloadUrl: String,
        onProgress: (progressPercent: Int, downloadedBytes: Long, totalBytes: Long) -> Unit
    ): File = withContext(Dispatchers.IO) {
        val updatesDir = File(context.cacheDir, "updates").apply {
            if (!exists()) mkdirs()
        }
        val targetFile = File(updatesDir, "GlobalCash-update.apk")
        if (targetFile.exists()) {
            targetFile.delete()
        }

        val request = Request.Builder()
            .url(downloadUrl)
            .header("User-Agent", "GlobalCash-Android-App")
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Download failed with HTTP code: ${response.code}")
        }

        val body = response.body ?: throw IOException("Empty response body from release asset")
        val contentLength = body.contentLength()
        val inputStream = body.byteStream()
        val outputStream = FileOutputStream(targetFile)

        val buffer = ByteArray(16 * 1024)
        var bytesRead: Int
        var totalBytesRead = 0L

        try {
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                val percent = if (contentLength > 0) {
                    ((totalBytesRead * 100) / contentLength).toInt().coerceIn(0, 100)
                } else {
                    -1
                }
                onProgress(percent, totalBytesRead, contentLength)
            }
            outputStream.flush()
        } finally {
            try { outputStream.close() } catch (_: Exception) {}
            try { inputStream.close() } catch (_: Exception) {}
        }

        targetFile
    }

    fun canRequestPackageInstalls(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun createInstallPermissionIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }

    fun installApk(apkFile: File) {
        val authority = "${context.packageName}.fileprovider"
        val contentUri: Uri = FileProvider.getUriForFile(context, authority, apkFile)

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(installIntent)
    }

    fun extractVersionNumbers(versionStr: String): List<Int> {
        val clean = versionStr
            .trim()
            .removePrefix("refs/tags/")
            .removePrefix("v")
            .removePrefix("V")
            .removePrefix("release-")
            .removePrefix("Release-")
            .trim()

        val regex = Regex("\\d+")
        return regex.findAll(clean).mapNotNull { it.value.toIntOrNull() }.toList()
    }

    fun isNewerVersion(current: String, remote: String): Boolean {
        val currentParts = extractVersionNumbers(current)
        val remoteParts = extractVersionNumbers(remote)

        // If either version has no extractable numbers, do NOT report an update to avoid false positives
        if (currentParts.isEmpty() || remoteParts.isEmpty()) {
            return false
        }

        // Compare each numeric component (major, minor, patch, build)
        val maxLength = maxOf(currentParts.size, remoteParts.size)
        for (i in 0 until maxLength) {
            val currNum = currentParts.getOrElse(i) { 0 }
            val remNum = remoteParts.getOrElse(i) { 0 }
            if (remNum > currNum) return true
            if (currNum > remNum) return false
        }

        return false
    }
}
