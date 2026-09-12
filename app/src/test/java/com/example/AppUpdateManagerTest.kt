package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.update.AppUpdateManager
import com.example.data.update.GitHubRelease
import com.example.data.update.GitHubReleaseAsset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppUpdateManagerTest {

    private lateinit var updateManager: AppUpdateManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        updateManager = AppUpdateManager(context)
    }

    @Test
    fun `test version comparison logic`() {
        // Newer versions
        assertTrue(updateManager.isNewerVersion(current = "1.0.0", remote = "1.0.1"))
        assertTrue(updateManager.isNewerVersion(current = "1.0.0", remote = "1.1.0"))
        assertTrue(updateManager.isNewerVersion(current = "1.0.0", remote = "2.0.0"))
        assertTrue(updateManager.isNewerVersion(current = "v1.0.0", remote = "v1.0.1"))
        assertTrue(updateManager.isNewerVersion(current = "1.0.9", remote = "1.0.10"))

        // Same versions
        assertFalse(updateManager.isNewerVersion(current = "1.0.0", remote = "1.0.0"))
        assertFalse(updateManager.isNewerVersion(current = "v1.0.0", remote = "1.0.0"))
        assertFalse(updateManager.isNewerVersion(current = "1.0.0", remote = "v1.0.0"))

        // Older versions
        assertFalse(updateManager.isNewerVersion(current = "1.0.1", remote = "1.0.0"))
        assertFalse(updateManager.isNewerVersion(current = "2.0.0", remote = "1.9.9"))
    }

    @Test
    fun `test find apk asset in release`() {
        val assets = listOf(
            GitHubReleaseAsset(
                id = 1L,
                name = "currency-converter-windows.zip",
                browserDownloadUrl = "https://example.com/windows.zip",
                size = 12000000L,
                contentType = "application/zip"
            ),
            GitHubReleaseAsset(
                id = 2L,
                name = "GlobalCash-app-debug.apk",
                browserDownloadUrl = "https://example.com/GlobalCash-app-debug.apk",
                size = 25000000L,
                contentType = "application/vnd.android.package-archive"
            ),
            GitHubReleaseAsset(
                id = 3L,
                name = "currency-converter-ios.zip",
                browserDownloadUrl = "https://example.com/ios.zip",
                size = 15000000L,
                contentType = "application/zip"
            )
        )

        val release = GitHubRelease(
            id = 101L,
            tagName = "v1.0.1",
            name = "GlobalCash v1.0.1",
            body = "Bug fixes and improvements",
            htmlUrl = "https://github.com/omriyosi/Currency-/releases/tag/v1.0.1",
            assets = assets
        )

        val apkAsset = release.findApkAsset()
        assertNotNull(apkAsset)
        assertEquals("GlobalCash-app-debug.apk", apkAsset?.name)
        assertEquals("https://example.com/GlobalCash-app-debug.apk", apkAsset?.downloadUrl)
    }

    @Test
    fun `test find apk asset when no apk exists`() {
        val release = GitHubRelease(
            id = 102L,
            tagName = "v1.0.2",
            name = "GlobalCash v1.0.2",
            body = "No android apk yet",
            htmlUrl = "https://github.com/omriyosi/Currency-/releases/tag/v1.0.2",
            assets = emptyList()
        )

        val apkAsset = release.findApkAsset()
        assertNull(apkAsset)
    }

    @Test
    fun `test default repository configuration`() {
        val (owner, repo) = updateManager.getStoredRepository()
        assertEquals("omriyosi", owner)
        assertEquals("Currency-", repo)

        updateManager.saveRepository("custom-user", "custom-repo")
        val (newOwner, newRepo) = updateManager.getStoredRepository()
        assertEquals("custom-user", newOwner)
        assertEquals("custom-repo", newRepo)

        // Reset
        updateManager.saveRepository(AppUpdateManager.DEFAULT_REPO_OWNER, AppUpdateManager.DEFAULT_REPO_NAME)
    }
}
