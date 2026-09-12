# GlobalCash 💱

A modern currency converter Android application built with Kotlin and Jetpack Compose. GlobalCash features real-time exchange rates, location-based local currency auto-detection, offline rate caching with Room database, customizable favorite currency pairs, and interactive conversion history.

---

## ✨ Features

- **Real-Time Exchange Rates**: Instant currency conversions across 150+ fiat and world currencies.
- **Auto-Detect Local Currency**: Automatically suggests your local currency based on device location or system locale.
- **Offline Mode & Room Caching**: Saves the most recent exchange rates locally so you can convert currencies anywhere, even without an internet connection.
- **Favorites & Watchlist**: Pin your most frequently used currency pairs for one-tap access.
- **Conversion History**: Keep track of previous calculations and exchange rate snapshots.
- **In-App GitHub Auto-Updates**: Automatically checks for new releases on GitHub, downloads new APK packages directly with progress indicators, and triggers seamless in-app installation.
- **Material Design 3**: Modern, responsive UI with smooth animations, dark mode support, and edge-to-edge layout.

---

## 🔄 In-App GitHub Auto-Updates

GlobalCash now includes a built-in auto-update system that integrates directly with GitHub Releases:

1. **Automatic Check on Startup**: When you open GlobalCash, the app checks the GitHub repository (`omriyosi/Currency-` by default) for newer release tags.
2. **Update Notification Banner & Top Bar Badge**: If a new release has been published with an APK asset, an update banner appears along with a badge on the system update icon in the top bar.
3. **One-Tap Download & Install**:
   - Tap **Update** to open the App Updates dialog.
   - Tap **Download & Install Update** to download the new APK with live progress tracking (percentage and MBs).
   - Once downloaded, GlobalCash uses Android's `FileProvider` and system installer to install the update seamlessly.
4. **Configurable Repository**: You can edit the target GitHub repository (Owner and Repo name) directly within the update dialog if you fork or mirror the project.

---

## 🚀 GitHub Actions Multi-Platform CI/CD Workflow

The repository includes a single, unified GitHub Actions workflow located at `.github/workflows/build-and-release.yml`.

### Parallel Jobs in the Workflow:
1. **`build-android`**:
   - Compiles the Android app using Gradle and Android SDK.
   - Automatically provisions/restores the debug keystore and environment configuration.
   - Produces `GlobalCash-Android.apk` and uploads it as a GitHub Actions artifact.
2. **`build-windows`**:
   - Packages the Windows distribution bundle (`GlobalCash-Windows-x64.zip`) on `windows-latest`.
   - Uploads the Windows package as an artifact.
3. **`build-ios`**:
   - Prepares the iOS package bundle (`GlobalCash-iOS-Universal.zip`) on `macos-latest`.
   - Uploads the iOS package as an artifact.
4. **`create-release`**:
   - Runs automatically on every push to `main`/`master`, on version tags, or on manual workflow dispatch.
   - Gathers all compiled files from the parallel jobs, computes SHA256 verification checksums, and publishes a new release in the repository's **Releases** tab with all assets attached.

### How Releases are Created
- **Automatic on Push**: Every commit pushed to `main`/`master` automatically compiles all platforms and publishes a release (e.g. `v1.0.1`, `v1.0.2`, etc.) marked as latest.
- **Git Tags**: Push a semantic version tag (e.g. `v1.0.0`) to generate a tagged milestone release:
  ```bash
  git tag v1.0.0
  git push origin v1.0.0
  ```
- **Manual Trigger**:
  1. Navigate to the **Actions** tab in your GitHub repository.
  2. Select **Multi-Platform Build & Release**.
  3. Click **Run workflow**, optionally specify a tag name, and click Run.

---

## 📱 Installing the Android APK

1. Go to the **Releases** tab in your GitHub repository.
2. Download `GlobalCash-Android.apk`.
3. Open the file on your Android phone or tablet.
4. When prompted, allow installing apps from your browser or file manager ("Install unknown apps").
5. Tap **Install** to start using GlobalCash.

---

## 🛠️ Local Development & Build

### Prerequisites
- JDK 21
- Android Studio Ladybug (or newer) / Android SDK (API 35+)

### Build Debug APK locally
```bash
# Clone the repository
git clone https://github.com/your-username/Currency-.git
cd Currency-

# Make gradlew executable (Linux/macOS)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug
```
The compiled APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

---

## 🏗️ Tech Stack & Architecture

- **Language**: Kotlin 2.2
- **UI Toolkit**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture
- **Local Persistence**: Room Database with Coroutines and StateFlow
- **Networking**: Ktor / OkHttp with JSON serialization
- **CI/CD**: GitHub Actions (Ubuntu, Windows, macOS runners)
