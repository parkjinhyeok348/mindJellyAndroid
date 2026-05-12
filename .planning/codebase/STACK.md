# Technology Stack

**Analysis Date:** 2026-05-12

## Languages

**Primary:**
- Java 11 - All application source code (`app/src/main/java/com/mindJellyProject/mindjelly/`)

**Secondary:**
- XML - Layout files, AndroidManifest, resource files (`app/src/main/res/`)

## Runtime

**Environment:**
- Android SDK 34 (Android 14) - compileSdk and targetSdk
- Minimum SDK: 31 (Android 12)

**Package Manager:**
- Gradle with Version Catalog (`gradle/libs.versions.toml`)
- Android Gradle Plugin: 8.7.3
- Lockfile: Not present

## Frameworks

**Core:**
- AndroidX AppCompat 1.6.1 - backward-compatible Activity/Fragment base
- AndroidX Activity 1.8.0 - ComponentActivity base for all screens
- AndroidX ConstraintLayout 2.1.4 - primary layout system
- Google Material Components 1.10.0 - UI widgets and theming
- AndroidX Lifecycle LiveData 2.6.1 - observable data for MVVM pattern
- AndroidX Lifecycle ViewModel 2.6.1 - ViewModel base class
- ViewBinding (enabled in `app/build.gradle`) - type-safe view references, no findViewById

**Networking:**
- Retrofit 2.9.0 - REST API client; singleton in `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java`
- OkHttp 4.9.0 - underlying HTTP transport for Retrofit
- Gson Converter 2.9.0 - JSON serialization/deserialization for Retrofit responses
- Gson 2.10.1 - JSON library

**Image Loading:**
- Glide 4.15.1 - remote image loading from API server URLs
  - Used in `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/BasicEmoAdapter.java`
  - Used in `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java`
  - Annotation processor: glide-compiler 4.15.1

**Splash Screen:**
- AndroidX Core SplashScreen 1.0.1 - API 31+ splash screen
  - Entry point: `app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java`

**Testing:**
- JUnit 4.13.2 - unit test runner
- AndroidX Test JUnit 1.1.5 - instrumented test runner (AndroidJUnitRunner)
- Espresso Core 3.5.1 - UI instrumented tests

**Build:**
- Android Gradle Plugin 8.7.3 - build toolchain

## Key Dependencies

**Critical:**
- `com.squareup.retrofit2:retrofit:2.9.0` - all backend communication; removing breaks all API calls
- `com.squareup.retrofit2:converter-gson:2.9.0` - required for JSON response parsing
- `com.squareup.okhttp3:okhttp:4.9.0` - HTTP transport layer
- `com.github.bumptech.glide:glide:4.15.1` - image display for emotion icons

**Infrastructure:**
- `androidx.lifecycle:lifecycle-livedata-ktx:2.6.1` - LiveData is the return type from all Repository methods
- `androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1` - ViewModel base for all screen ViewModels
- `androidx.annotation:annotation:1.6.0` - nullability and threading annotations

**Declared in version catalog but NOT added to app/build.gradle:**
- `com.amazonaws:aws-java-sdk-s3:1.12.767` - in `gradle/libs.versions.toml`, not used in app module
- `io.awspring.cloud:spring-cloud-aws-starter:3.1.1` - in `gradle/libs.versions.toml`, not used in app module

## Configuration

**Environment:**
- Base URL hardcoded in `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java`: `http://10.0.2.2:8080/`
- `10.0.2.2` is the Android emulator loopback address pointing to host machine localhost
- `android:usesCleartextTraffic="true"` in `app/src/main/AndroidManifest.xml` - permits HTTP (non-TLS) traffic
- No environment variable injection or properties-based config file detected

**Build:**
- `app/build.gradle` - module-level build configuration
- `gradle/libs.versions.toml` - centralized version catalog
- `proguard-rules.pro` - ProGuard config; minification disabled (minifyEnabled false) in release builds

## Platform Requirements

**Development:**
- Android Studio with AGP 8.7.3 support
- JDK 11 (sourceCompatibility and targetCompatibility both set to VERSION_11)
- Android emulator or physical device running Android 12+ (minSdk 31)

**Production:**
- Android 12-14 (API 31-34)
- Requires backend server accessible at the configured base URL
- INTERNET permission declared in `app/src/main/AndroidManifest.xml`

---

*Stack analysis: 2026-05-12*
