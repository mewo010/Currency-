# GlobalCash 💱

A modern currency converter Android application built with Kotlin and Jetpack Compose. GlobalCash features real-time exchange rates, location-based local currency auto-detection, offline rate caching with Room database, customizable favorite currency pairs, and interactive conversion history.

---

## ✨ Features

- **Real-Time Exchange Rates**: Instant currency conversions across 150+ fiat and world currencies.
- **Auto-Detect Local Currency**: Automatically suggests your local currency based on device location or system locale.
- **Offline Mode & Room Caching**: Saves the most recent exchange rates locally so you can convert currencies anywhere, even without an internet connection.
- **Favorites & Watchlist**: Pin your most frequently used currency pairs for one-tap access.
- **Conversion History**: Keep track of previous calculations and exchange rate snapshots.
- **Material Design 3**: Modern, responsive UI with smooth animations, dark mode support, and edge-to-edge layout.

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
   - Triggered when a version tag (e.g. `v1.0.0`) is pushed or when run manually via the GitHub **Actions** tab with "Publish build artifacts as a new GitHub Release" enabled.
   - Gathers all compiled files from the parallel jobs, computes SHA256 verification checksums, and publishes a new release in the repository's **Releases** tab.

### How to Create a Release
- **Option 1 (Git Tag)**:
  ```bash
  git tag v1.0.0
  git push origin v1.0.0
  ```
- **Option 2 (GitHub Actions UI)**:
  1. Navigate to the **Actions** tab in your GitHub repository.
  2. Select **Multi-Platform Build & Release**.
  3. Click **Run workflow**, check the release option, specify your tag name (e.g., `v1.0.0`), and run.

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
