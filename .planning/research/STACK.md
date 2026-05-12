# Stack Research — MindJelly Android
<!-- researched: 2026-05-12 | confidence: HIGH (versions verified against AndroidX releases) -->

## Current Stack (Existing)

| Component | Current | Status |
|-----------|---------|--------|
| Language | Java | Keep — no Kotlin migration for v1 |
| Min/Target SDK | TBD (check libs.versions.toml) | Verify |
| Retrofit2 | In use | Upgrade to 2.11.0 |
| OkHttp | Implicit via Retrofit | Add explicit 4.12.0 dependency |
| Gson Converter | In use | Keep |
| AndroidX AppCompat | In use | Upgrade to 1.7.1 |
| LiveData/ViewModel | In use | Upgrade Lifecycle to 2.10.0 |
| RecyclerView | In use | Upgrade to 1.4.0, migrate to ListAdapter |

## Recommended Additions for v1

### Authentication Token Storage
**Library:** `androidx.security:security-crypto:1.1.0`
**Why:** EncryptedSharedPreferences is the pragmatic JWT storage choice for Java-only v1. Direct Keystore API is 3x more boilerplate with same security model. Deprecated-but-functional — no replacement exists yet.
**Use:** Store JWT access token + refresh token after login. Read in `AuthInterceptor`.

### Auth Header Injection
**Library:** OkHttp `Interceptor` (no new library — included with OkHttp)
**Why:** All API calls need `Authorization: Bearer <token>` header. Retrofit `@Header` per-call approach doesn't scale.
**Pattern:**
```java
// AuthInterceptor.java
public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = TokenManager.getToken();
        Request request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer " + token)
            .build();
        return chain.proceed(request);
    }
}
// RetrofitClient.java — refactor to accept OkHttpClient
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new AuthInterceptor())
    .build();
```
**Note:** `RetrofitClient.java` needs refactoring — currently uses no `OkHttpClient`.

### 7-Day Aging Background Trigger
**Library:** `androidx.work:work-runtime:2.11.2` (March 2026)
**Why:** WorkManager is the only reliable Android background task mechanism that survives Doze mode, battery optimization, and app restarts. AlarmManager is fragile; plain threads don't survive.
**Architecture:** Server holds aging state. WorkManager triggers a daily check — server transitions jelly to "matured" when 7 days have passed.
```java
PeriodicWorkRequest agingCheck = new PeriodicWorkRequest.Builder(
    AgingCheckWorker.class, 1, TimeUnit.DAYS)
    .setConstraints(new Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build())
    .build();
WorkManager.getInstance(context)
    .enqueueUniquePeriodicWork("aging_check",
        ExistingPeriodicWorkPolicy.KEEP, agingCheck);
```

### Adapter Upgrade (RecyclerView)
**Pattern:** Migrate `JellyDrawerAdapter` and `BasicEmoAdapter` from `RecyclerView.Adapter` to `ListAdapter + DiffUtil`
**Why:** Prevents full-list redraws on every LiveData emission. `submitList()` handles diffs automatically.
**Library:** Already included — `recyclerview:1.4.0`

## Version Upgrades Recommended (Phase 1)

| Library | Current | Target | Notes |
|---------|---------|--------|-------|
| `androidx.appcompat:appcompat` | Unknown | `1.7.1` | June 2025 |
| `androidx.lifecycle:lifecycle-viewmodel` | Unknown | `2.10.0` | November 2025 |
| `androidx.lifecycle:lifecycle-livedata` | Unknown | `2.10.0` | November 2025 |
| `androidx.recyclerview:recyclerview` | Unknown | `1.4.0` | January 2025 |
| `com.squareup.retrofit2:retrofit` | Unknown | `2.11.0` | Latest stable |
| `com.squareup.okhttp3:okhttp` | Unknown | `4.12.0` | Latest stable |
| `com.google.android.material:material` | Unknown | `1.12.0` | Latest stable |

## What NOT to Add (v1)

| Library | Why Skip |
|---------|---------|
| Room DB | No offline-first requirement in PROJECT.md. Pure Retrofit + ViewModel LiveData cache is sufficient. Add after user feedback on data loss. |
| Hilt/Dagger | DI adds setup complexity for 1-person project. Manual ViewModel factory pattern is sufficient for v1. |
| Kotlin Coroutines | Java project — mixing Kotlin coroutines adds complexity. LiveData + Callback pattern is already established. |
| Glide/Picasso | Only needed if emotion icons are remote URLs. If icons are local drawables/assets, skip entirely. |
| Firebase | No push notifications, no analytics in v1 scope. |
| DataStore | EncryptedSharedPreferences is sufficient for JWT storage. DataStore migration is a v2 concern. |

## Roadmap Implications

- **Phase 1 (Auth):** Add `security-crypto`, implement `AuthInterceptor`, refactor `RetrofitClient` to use `OkHttpClient`, remove `usesCleartextTraffic=true` for release builds
- **Phase 2 (Core Loop):** Upgrade RecyclerView to 1.4.0, migrate adapters to `ListAdapter + DiffUtil`
- **Phase 3 (AgingRoom):** Add WorkManager, implement `AgingCheckWorker`, enqueue at app start in `SplashActivity`
- **Version bumps:** All AndroidX upgrades should land in Phase 1 before new feature code

## Open Questions

- Does the Spring Boot backend issue both access token + refresh token, or single JWT? Affects `AuthInterceptor` 401 handling.
- Emotion icons — local drawable assets or remote URLs? Determines if Glide is needed.
- `usesCleartextTraffic` — production backend URL (HTTPS) vs dev (HTTP emulator)? Use `network_security_config.xml` to scope cleartext to debug only.
