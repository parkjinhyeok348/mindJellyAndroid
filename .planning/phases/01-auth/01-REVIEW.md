---
phase: 01-auth
reviewed: 2026-05-14T00:00:00Z
depth: standard
files_reviewed: 21
files_reviewed_list:
  - app/src/main/java/com/mindJellyProject/mindjelly/common/SessionManager.java
  - app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java
  - app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java
  - app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java
  - app/src/main/java/com/mindJellyProject/mindjelly/users/retrofit/UserRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java
  - app/src/main/java/com/mindJellyProject/mindjelly/users/view/SignUpActivity.java
  - app/src/main/java/com/mindJellyProject/mindjelly/users/viewmodel/UserViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java
  - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmo/retrofit/AgedEmoRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmo/viewmodel/AgedEmoViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmoImage/retrofit/AgedEmoImageRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmoImage/viewmodel/AgedEmoImageViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/retrofit/BasicEmoRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/viewmodel/BasicEmoViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/viewmodel/JellyViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/viewmodel/JellyCombViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyImage/retrofit/JellyImageRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyImage/viewmodel/JellyImageViewModel.java
findings:
  critical: 6
  warning: 6
  info: 3
  total: 15
status: issues_found
---

# Phase 01: Auth 기반 Code Review Report

**Reviewed:** 2026-05-14T00:00:00Z
**Depth:** standard
**Files Reviewed:** 21
**Status:** issues_found

## Summary

This phase implements JWT-based authentication for the MindJelly Android app, covering a SessionManager backed by EncryptedSharedPreferences, an OkHttp AuthInterceptor, a context-aware RetrofitClient singleton, three auth-flow Activities (Splash, Login, SignUp), and migration of six domain repositories and their ViewModels to the new context-aware Retrofit path.

The core cryptographic decisions (EncryptedSharedPreferences with AES256-GCM, no plaintext token storage) are sound. However, there are multiple critical issues: a plaintext HTTP base URL with no TLS, a `@Deprecated` no-auth Retrofit path that remains callable in production code, a leaked `Response` body on 401 that can block the OkHttp connection pool, an unresolvable `LoginActivity` navigation target on "find email", and silent data-loss paths in several repositories where a null response body is surfaced as success.

---

## Critical Issues

### CR-01: Plaintext HTTP base URL — all traffic (including JWT) is sent unencrypted

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java:23`

**Issue:** `BASE_URL` is `http://10.0.2.2:8080/`. Every API request, including the login response that carries the JWT token and all subsequent Bearer-authenticated calls, travels over cleartext HTTP. An adversary on the same network (or any network segment between the emulator and the host) can read the token in transit. Android 9+ enforces cleartext traffic restrictions by default; apps that ship with this URL will also fail on physical devices unless `android:usesCleartextTraffic="true"` is set in the manifest — which itself is a security misconfiguration.

**Fix:** Use `https://` for all non-emulator targets. For emulator-only development, add an explicit `network_security_config.xml` that restricts the `http://` exception to `10.0.2.2` only, with a comment that it must be removed before release:

```xml
<!-- res/xml/network_security_config.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <!-- DEVELOPMENT ONLY — remove before release -->
        <domain includeSubdomains="false">10.0.2.2</domain>
    </domain-config>
    <base-config cleartextTrafficPermitted="false"/>
</network-security-config>
```

---

### CR-02: `@Deprecated` `createService` overload bypasses AuthInterceptor entirely

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java:54-61`

**Issue:** The no-arg `createService(Class<T>)` overload constructs a brand-new, bare `Retrofit` instance with no OkHttp client and no `AuthInterceptor`. Any call site that accidentally uses this overload — or that is added in the future without noticing the ambiguity — will send unauthenticated requests with no Bearer token. The `@Deprecated` annotation alone does not prevent compilation or runtime use. Because `createService` is a generic static method, IDE auto-complete will suggest both overloads and a developer can easily pick the wrong one.

**Fix:** Remove the method entirely. All repositories have been migrated to the context-aware path. If a compile-time safety net is still needed during transition, throw `UnsupportedOperationException` at runtime instead of silently creating a second, unauthenticated client:

```java
@Deprecated
public static <T> T createService(Class<T> serviceClass) {
    throw new UnsupportedOperationException(
        "Use createService(Class, Context) — no-context path is removed.");
}
```

---

### CR-03: AuthInterceptor leaks the 401 `Response` body, risking OkHttp connection pool exhaustion

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java:42-48`

**Issue:** When a 401 is received, the interceptor clears the session and starts the login `Activity`, then returns the `Response` object to the caller. The `Response` body's `ResponseBody` is a streaming resource backed by an OkHttp connection. Because the body is never closed (no `response.body().close()` or `try-with-resources`), the underlying TCP connection is never returned to the OkHttp connection pool. Under repeated 401s (e.g., a token that expires mid-session with several queued requests), this will exhaust the pool and cause all subsequent network calls to hang until the idle timeout fires.

**Fix:** Close the body before returning when a 401 is detected:

```java
if (response.code() == 401) {
    response.close(); // release the connection back to the pool
    SessionManager.getInstance(appContext).clear();
    RetrofitClient.reset();
    Intent intent = new Intent(appContext, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    appContext.startActivity(intent);
}
return response;
```

---

### CR-04: Incorrect navigation target for "find email" button — opens `MainActivity` instead of a find-email screen

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java:90-93`

**Issue:** The `btnFindEamil` click handler navigates to `MainActivity`, not to any find-email screen. The comment even says "비밀번호 찾기 페이지로 이동" (navigate to find-password page), confirming this is a copy-paste error. A user who taps "Find Email" is silently deposited into the main application without having authenticated through the normal login flow, bypassing the session check that `SplashActivity` performs.

```java
btnFindEamil.setOnClickListener(v -> {
    // 비밀번호 찾기 페이지로 이동   <-- wrong comment, wrong target
    Intent intent = new Intent(LoginActivity.this, MainActivity.class);  // BUG
    startActivity(intent);
});
```

**Fix:** Navigate to the correct find-email activity. Until that activity exists, disable the button or show a "not yet implemented" message rather than silently landing in `MainActivity`:

```java
btnFindEamil.setOnClickListener(v -> {
    Intent intent = new Intent(LoginActivity.this, FindEmailActivity.class);
    startActivity(intent);
});
```

---

### CR-05: `SignUpActivity.registerUser()` treats `resource.getData() != null` as success — error responses with a non-null body are silently treated as success

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/users/view/SignUpActivity.java:83`

**Issue:** The observer checks `resource.getData() != null` to decide whether registration succeeded. `Resource.success(response.body())` is called for any `response.isSuccessful()` result in `UserRepository.createUser()`, so this works in the happy path. However, if the server returns a 2xx response with a non-null body but the registration actually failed at the application level (e.g., the server returns `{"status":"error","message":"..."}` with HTTP 200), the UI will show "발송된 이메일을 확인하세요" and navigate to `LoginActivity`. This is a protocol-level mismatch that depends on the server contract, but the check is also asymmetric with how `LoginActivity` handles the analogous case (it correctly checks `resource.isSuccess()`).

More immediately: `UserRepository.createUser()` calls `Resource.success(response.body())` without a null check on `response.body()`. A 204 No Content or other body-less 2xx from the server will produce `Resource.success(null)`, and since `getData()` returns `null`, the observer will fall into the else-branch and show an error — the opposite of the intended behavior described in the comment.

**Fix:** Check both `isSuccess()` and a non-null body consistently, or align the repository to guard the null case:

```java
// In SignUpActivity observer
userViewModel.createUser(userSaveReqDTO).observe(this, resource -> {
    if (resource != null && resource.isSuccess()) {
        Toast.makeText(this, "발송된 이메일을 확인하세요.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    } else { ... }
});
```

---

### CR-06: `SessionManager` constructor throws `RuntimeException` — application crashes silently on EncryptedSharedPreferences failure

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/common/SessionManager.java:47-49`

**Issue:** If `EncryptedSharedPreferences.create()` throws `GeneralSecurityException` or `IOException` (e.g., corrupted keystore after a failed OS upgrade, device in a low-memory state during first boot, or a backup-restore cycle that corrupts the keystore), the app will crash with an unhandled `RuntimeException` at startup. This is especially problematic because `SplashActivity` calls `SessionManager.getInstance()` on the main thread: the crash will occur before the user sees any UI and with no recoverable error message.

**Fix:** Either handle the exception gracefully (fall back to regular `SharedPreferences` as a last resort, or show a user-facing error and offer a "clear data" option), or at minimum, provide a fallback that allows the app to boot into an unauthenticated state and re-prompt for login:

```java
private SessionManager(Context context) {
    SharedPreferences prefs;
    try {
        MasterKey masterKey = new MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();
        prefs = EncryptedSharedPreferences.create(
                context, PREF_NAME, masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
    } catch (GeneralSecurityException | IOException e) {
        Log.e(TAG, "EncryptedSharedPreferences init failed, clearing and retrying", e);
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
        // Surface error to user rather than hard crash
        prefs = context.getSharedPreferences(PREF_NAME + "_fallback", Context.MODE_PRIVATE);
    }
    encryptedPrefs = prefs;
}
```

---

## Warnings

### WR-01: JWT payload decoded without padding normalization — `extractAndSaveUserId` can silently fail on standard JWTs

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/common/SessionManager.java:67-86`

**Issue:** Base64url-encoded JWT payloads omit padding characters (`=`). `Base64.NO_PADDING` tells Android's decoder to *not add* padding when encoding, but it does **not** instruct the decoder to tolerate missing padding on input. Depending on the JWT payload length, the payload string may or may not be a multiple of 4 characters. When it is not, `Base64.decode()` with `URL_SAFE | NO_PADDING | NO_WRAP` may throw `IllegalArgumentException` or silently produce truncated output. The caught `IllegalArgumentException` means a failed decode is silently swallowed — `userId` is never written — and all subsequent operations that depend on `getUserId()` receive `-1L` instead.

**Fix:** Manually re-pad the Base64url string before decoding:

```java
private String padBase64(String base64url) {
    int padding = (4 - base64url.length() % 4) % 4;
    StringBuilder sb = new StringBuilder(base64url);
    for (int i = 0; i < padding; i++) sb.append('=');
    return sb.toString();
}

// In extractAndSaveUserId:
byte[] decoded = Base64.decode(padBase64(payload), Base64.URL_SAFE);
```

---

### WR-02: `RetrofitClient.reset()` nullifies the singleton but in-flight requests continue using the old (already-cleared) session token

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java:44` / `RetrofitClient.java:64-66`

**Issue:** On a 401, `AuthInterceptor` calls `RetrofitClient.reset()` which sets `instance = null`. Any other thread that subsequently calls `RetrofitClient.getInstance(context)` gets a new, freshly-constructed client with a cleared session. However, all other requests that are already in-flight at the same moment share the same `OkHttpClient` (and thus the same `AuthInterceptor` instance). Those requests will each also hit 401, each call `reset()` (a no-op after the first), and each trigger `startActivity(LoginActivity)`. The result is multiple redundant `LoginActivity` launches stacked on the back stack, which is confusing and wastes memory.

**Fix:** Gate the 401 handling with a flag to ensure only the first thread acts:

```java
private final AtomicBoolean isLoggingOut = new AtomicBoolean(false);

// in intercept():
if (response.code() == 401 && isLoggingOut.compareAndSet(false, true)) {
    response.close();
    SessionManager.getInstance(appContext).clear();
    RetrofitClient.reset();
    Intent intent = new Intent(appContext, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    appContext.startActivity(intent);
}
```

---

### WR-03: `SplashActivity` runs session check inside a 2-second delay that is mislabeled "1초"

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java:42`

**Issue:** `postDelayed(..., 2000)` introduces a 2-second mandatory wait before routing. The inline comment says `// 1초`. Beyond the documentation mismatch, forcing users to wait 2 seconds before entering the app on every cold start is a user-experience problem. More importantly, `hasToken()` is a synchronous `SharedPreferences` read — there is no I/O reason to delay it at all.

**Fix:** Remove the artificial delay. If a branded splash screen is required, use the Android 12+ `SplashScreen` API (the class is already annotated with `@SuppressLint("CustomSplashScreen")`), or at minimum correct the comment and reduce the delay to the minimum needed for animation:

```java
// Remove postDelayed wrapper; route immediately
SessionManager sessionManager = SessionManager.getInstance(SplashActivity.this);
if (sessionManager.hasToken()) {
    startActivity(new Intent(SplashActivity.this, MainActivity.class));
} else {
    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
}
finish();
```

---

### WR-04: `LoginActivity` leaks a LiveData observer — calling `login` multiple times (double-tap) registers multiple observers

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java:63-79`

**Issue:** Each tap of `btnLogin` calls `userViewModel.login(reqDTO).observe(this, ...)`, adding a new observer to a new `LiveData` instance returned from the repository. If the user taps the button rapidly (or the first tap is slow to respond), multiple in-flight calls are pending and multiple observers are registered. Each call that succeeds will independently call `sessionManager.saveToken()` and launch `MainActivity`, producing multiple stacked instances of `MainActivity`. This is especially problematic because the login button is not disabled during the in-flight request.

**Fix:** Disable the button when the request is submitted, and re-enable it only on failure; or cache the LiveData in the ViewModel and observe a single `MutableLiveData` rather than calling observe on a new LiveData each time:

```java
btnLogin.setOnClickListener(v -> {
    btnLogin.setEnabled(false);
    UserLoginReqDTO reqDTO = new UserLoginReqDTO(
            etEmail.getText().toString().trim(),
            etPassword.getText().toString().trim());
    userViewModel.login(reqDTO).observe(this, resource -> {
        btnLogin.setEnabled(true);
        if (resource != null && resource.isSuccess() && resource.getData() != null) {
            sessionManager.saveToken(resource.getData());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else { /* show error */ }
    });
});
```

---

### WR-05: `SignUpActivity` passes `null` hardcoded for `profileImage` field in `UserSaveReqDTO`

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/users/view/SignUpActivity.java:79`

**Issue:** `UserSaveReqDTO` is constructed with `null` as the `profileImage` argument (position 8). This is a silent, hardcoded null that will be serialized by Gson into an absent or `null` JSON field. If the server treats a missing `profileImage` as a validation failure, registration will silently fail for every user. This is not a placeholder — the UI has no profile image field, making it a permanent null that cannot be changed without a code release.

**Fix:** Confirm the server contract. If `profileImage` is optional, document the intent explicitly with a named constant rather than a raw `null`:

```java
private static final String NO_PROFILE_IMAGE = null; // server accepts absent field

UserSaveReqDTO userSaveReqDTO = new UserSaveReqDTO(
    mobilePhoneNumber, email, password, userName, nickName, gender,
    birthDate, NO_PROFILE_IMAGE, ageRange, isMarketing
);
```

---

### WR-06: `BasicEmoRepository` does not null-check `response.body()` before passing to `Resource.success` — null body treated as success

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/retrofit/BasicEmoRepository.java:41-42` and `63-64`

**Issue:** Both `getBasicEmoById` and `getAllBasicEmoList` call `Resource.success(response.body())` whenever `response.isSuccessful()` is true, without checking for a null body. A 204 No Content response — or any 2xx with an empty body — will produce `Resource.success(null)`. Consumers that call `resource.getData()` without a null check will throw a `NullPointerException`. This inconsistency also exists in `JellyRepository` (`createJelly`, `getJellyInfo`, `updateJelly`, `getJellyById`, `getJellyList`) which follows the same pattern.

**Fix:** Add a null body check consistent with the pattern already used in `AgedEmoRepository` and `UserRepository`:

```java
if (response.isSuccessful() && response.body() != null) {
    resultLiveData.postValue(Resource.success(response.body()));
} else {
    resultLiveData.postValue(Resource.error("Error: " + response.message()));
}
```

---

## Info

### IN-01: `Log.d` statements left in production code in `JellyDrawerActivity`

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java:34, 62, 67`

**Issue:** Three `Log.d` calls log user-identifiable data (the `userId` integer) and internal state to logcat. On release builds these are accessible via `adb logcat` on non-rooted devices and can expose user IDs to other apps that hold `READ_LOGS` permission on older Android versions.

**Fix:** Either remove the debug logs or guard them with `BuildConfig.DEBUG`:

```java
if (BuildConfig.DEBUG) {
    Log.d(TAG, "Requesting jelly list for userId: " + userId);
}
```

---

### IN-02: `btnFindEamil` — typo in field name ("Eamil" instead of "Email")

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java:26, 46, 89`

**Issue:** The field is named `btnFindEamil` (transposed letters). While this does not affect runtime behavior, it degrades readability and searchability and is likely to be perpetuated in future code that references this field.

**Fix:** Rename to `btnFindEmail` and update the corresponding view ID in the layout file if it also has the typo.

---

### IN-03: `RetrofitClient` `@Deprecated` no-context overload has no `@NonNull` annotation and no Javadoc explaining why it is deprecated

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java:53-61`

**Issue:** The deprecated method has only an inline comment. No `@deprecated` Javadoc tag is present, so IDEs will not render the deprecation explanation in tooltips, and the comment explaining the migration path is invisible at call sites.

**Fix:** Add a Javadoc `@deprecated` tag:

```java
/**
 * @deprecated Use {@link #createService(Class, Context)} instead.
 *             This path creates an unauthenticated Retrofit instance with no Bearer token.
 *             Will be removed once all repositories are migrated.
 */
@Deprecated
public static <T> T createService(Class<T> serviceClass) { ... }
```

---

_Reviewed: 2026-05-14T00:00:00Z_
_Reviewer: Claude (gsd-code-reviewer)_
_Depth: standard_
