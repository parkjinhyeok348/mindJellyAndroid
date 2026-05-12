# Technical Concerns
<!-- last_mapped: 2026-05-12 -->

## Summary

Active Android project (MindJelly) with MVVM architecture, partially implemented. Several screens and features are wired up but incomplete. Core concerns are: hardcoded dev server URL, cleartext HTTP in production manifest, missing loading state in Resource wrapper, and multiple empty click handlers.

---

## High Priority

### 1. Hardcoded Emulator Localhost URL
**File:** `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java:20`
**Issue:** `BASE_URL = "http://10.0.2.2:8080/"` — this is the Android emulator's alias for the dev machine's localhost. This will fail on real devices and in any non-local environment.
**Impact:** App cannot connect to backend on physical devices or production.
**Fix:** Move BASE_URL to `BuildConfig`, environment-specific `gradle.properties`, or a config file.

### 2. Cleartext HTTP Traffic Allowed
**File:** `app/src/main/AndroidManifest.xml:15`
**Issue:** `android:usesCleartextTraffic="true"` — all network traffic is sent unencrypted.
**Impact:** Security vulnerability; Google Play may flag this; credentials/tokens sent in plaintext.
**Fix:** Switch to HTTPS for backend; remove `usesCleartextTraffic` or scope it to debug builds only.

### 3. Incomplete Activity Implementations
**File:** `app/src/main/java/com/mindJellyProject/mindjelly/MainActivity.java:42-61`
**Issue:** Three button click handlers (`btnAgingRoom`, `btnJellyMuseum`, `btnSelectionBox`) have empty `onClick` implementations — navigation not wired.
**Impact:** Tapping these buttons does nothing; core app screens unreachable from home.
**Fix:** Add `Intent` navigation to `AgingRoomActivity`, `jellyMuseumActivity`, `JellySelectionBoxActivity`.

---

## Medium Priority

### 4. No Loading State in `Resource<T>`
**File:** `app/src/main/java/com/mindJellyProject/mindjelly/common/Resource.java`
**Issue:** `Resource` only has `success` and `error` states — no `loading` state. Views cannot show loading indicators while waiting for API responses.
**Impact:** Poor UX (no progress feedback); potential for stale data display during network calls.
**Fix:** Add `Resource.loading()` factory method and `Status` enum (`LOADING`, `SUCCESS`, `ERROR`).

### 5. Repository Classes in `retrofit/` Package
**File:** All `*/retrofit/XxxRepository.java` files
**Issue:** Repository classes (data access layer) are placed inside packages named `retrofit/`, which is misleading — these are not Retrofit configuration classes.
**Impact:** Makes codebase harder to navigate; violates separation of concerns naming.
**Fix:** Move repositories to `*/repository/` packages; keep only `*Service.java` interfaces in `*/retrofit/`.

### 6. No Dependency Injection
**Files:** All `*ViewModel.java` files (e.g., `JellyViewModel.java:33`)
**Issue:** ViewModels instantiate Repositories directly (`new JellyRepository()`). No DI framework used.
**Impact:** Tight coupling makes unit testing difficult; can't swap implementations; no lifecycle management for repositories.
**Fix:** Adopt Hilt/Dagger for DI, or at minimum use a `ViewModelFactory` pattern.

### 7. No Authentication Token Management
**Files:** `users/retrofit/UserRepository.java`, `common/RetrofitClient.java`
**Issue:** No JWT/session token storage or injection into request headers visible. No `OkHttpClient` interceptor for auth headers.
**Impact:** Authenticated API endpoints will fail without token; user sessions not persisted.
**Fix:** Add `OkHttpClient` with `AuthInterceptor`; store token in `SharedPreferences` or `EncryptedSharedPreferences`.

---

## Low Priority

### 8. Activity Naming Inconsistency
**Files:** `agedEmoDomain/agedEmo/view/jellyMuseumActivity.java`
**Issue:** `jellyMuseumActivity` starts with lowercase `j`, violating Java class naming conventions (all other Activities use PascalCase).
**Impact:** Minor — cosmetic inconsistency, but can cause confusion.
**Fix:** Rename to `JellyMuseumActivity` (update manifest reference too).

### 9. Retrofit Singleton Not Thread-Safe (Minor)
**File:** `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java:23-28`
**Issue:** `getInstance()` uses a non-synchronized null check. In multi-threaded scenarios, multiple Retrofit instances could be created.
**Impact:** Low risk in practice (Android main thread initialization), but technically unsafe.
**Fix:** Use `synchronized` block or initialize as a static final field.

### 10. No Error Handling UI Pattern
**Files:** Various `*Activity.java` view files
**Issue:** No standardized approach to displaying API errors to users (toasts, snackbars, error views).
**Impact:** Errors from `Resource.error()` may be silently ignored or inconsistently handled.
**Fix:** Establish a shared error display utility; enforce consistent error observation in activities.

---

## Security Checklist

| Issue | Severity | Status |
|-------|----------|--------|
| Cleartext traffic enabled | High | Open |
| No HTTPS | High | Open |
| No auth token management | High | Open |
| Hardcoded dev URL | Medium | Open |
| No `EncryptedSharedPreferences` for tokens | Medium | Not yet applicable |

---

## TODOs Found in Source

No explicit `// TODO` comments found in source files. Incomplete implementations are indicated by empty click handlers in `MainActivity.java`.
