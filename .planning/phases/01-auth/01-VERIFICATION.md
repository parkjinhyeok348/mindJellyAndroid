---
phase: 01-auth
verified: 2026-05-14T10:00:00Z
status: gaps_found
score: 3/4 must-haves verified
overrides_applied: 0
gaps:
  - truth: "이메일 인증 미완료 응답 시 이메일 인증이 필요합니다 메시지가 표시된다"
    status: failed
    reason: >
      UserRepository.login()이 실패 시 Resource.error("Error: " + response.message())를
      사용한다. response.message()는 HTTP 상태 이유 구문(예: "Unauthorized")이며
      서버가 응답 본문에 전달하는 EMAIL_NOT_VERIFIED 메시지를 읽지 않는다.
      따라서 LoginActivity.isEmailVerificationRequired()의 조건
      (errorMessage.contains("EMAIL_NOT_VERIFIED") || errorMessage.contains("이메일 인증"))이
      실제 서버 응답에서는 절대 충족되지 않는다. errorBody()를 읽어야 한다.
    artifacts:
      - path: "app/src/main/java/com/mindJellyProject/mindjelly/users/retrofit/UserRepository.java"
        issue: "login() 메서드 line 216: response.message() 사용 — 응답 본문 대신 HTTP 이유 구문만 전달됨"
      - path: "app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java"
        issue: "isEmailVerificationRequired() 메서드는 올바르게 작성됐으나 UserRepository에서 올바른 오류 메시지를 받지 못함"
    missing:
      - >
        UserRepository.login() 실패 분기에서 checkDuplicateEmail()처럼 getLoginErrorMessage(response)를
        호출하도록 수정: resultLiveData.postValue(Resource.error(getLoginErrorMessage(response)));
        그래야 서버가 응답 본문에 보내는 EMAIL_NOT_VERIFIED 메시지가 LoginActivity까지 전달된다.
---

# Phase 1: Auth 기반 Verification Report

**Phase Goal:** 실제 사용자로 로그인하고 인증된 API 호출이 가능한 앱 완성
**Verified:** 2026-05-14T10:00:00Z
**Status:** gaps_found
**Re-verification:** No — initial verification

---

## Goal Achievement

### Observable Truths

| # | Truth | Status | Evidence |
|---|-------|--------|----------|
| 1 | 신규 사용자가 이메일 + 비밀번호로 회원가입하고 이메일 인증을 받을 수 있다 | PARTIAL | SignUpActivity observes createUser(), shows "발송된 이메일을 확인하세요." and navigates to LoginActivity — signup flow VERIFIED. Email verification UX in LoginActivity is wired but broken (see gap below). |
| 2 | 가입된 사용자가 로그인 후 앱을 재시작해도 세션이 유지된다 | VERIFIED | SessionManager uses EncryptedSharedPreferences (AES256_GCM). SplashActivity calls SessionManager.getInstance().hasToken() and routes to MainActivity when true. |
| 3 | 모든 API 요청에 JWT Bearer 토큰이 자동으로 포함된다 (AuthInterceptor 동작 확인) | VERIFIED | AuthInterceptor.intercept() reads token from SessionManager, injects "Authorization: Bearer " + token header. All 7 Repositories use RetrofitClient.createService(Class, context) — no deprecated context-free calls remain. |
| 4 | JellyDrawerActivity에서 userId 하드코딩(1L)이 제거되고 JWT에서 추출된 userId가 사용된다 | VERIFIED | No "= 1L" literal in JellyDrawerActivity. SessionManager.getUserId() called at line 53. -1L sentinel check correctly guards unauthenticated access. Java auto-boxing (long -> Long) is safe. |

**Score:** 3/4 truths verified (Truth 1 is PARTIAL due to broken email verification error path)

---

## Required Artifacts

| Artifact | Expected | Status | Details |
|----------|----------|--------|---------|
| `app/src/main/java/com/mindJellyProject/mindjelly/common/SessionManager.java` | JWT 저장/조회/삭제 + userId 추출 | VERIFIED | All 6 public methods present. EncryptedSharedPreferences + AES256_GCM. Base64.URL_SAFE\|NO_PADDING\|NO_WRAP. getUserId() returns primitive long, default -1L. |
| `gradle/libs.versions.toml` | securityCrypto = "1.1.0-alpha06" | VERIFIED | Line 19: `securityCrypto = "1.1.0-alpha06"`, line 41: `security-crypto` library alias present. |
| `app/build.gradle` | implementation libs.security.crypto | VERIFIED | Line 43: `implementation libs.security.crypto` present. |
| `app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java` | OkHttp Bearer 토큰 주입 인터셉터 | VERIFIED | Injects "Bearer " + token when token != null. 401 response triggers SessionManager.clear() + RetrofitClient.reset() + LoginActivity intent with FLAG_ACTIVITY_CLEAR_TASK. |
| `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java` | OkHttpClient + AuthInterceptor 포함 Retrofit 싱글턴 | VERIFIED | Context-based singleton. addInterceptor(new AuthInterceptor(appContext)). reset() method present. Deprecated no-context overload present (intentional bridge — no callers remain). |
| `app/src/main/res/xml/network_security_config.xml` | debug 전용 cleartext 허용 설정 | VERIFIED | Main config: `cleartextTrafficPermitted="false"`. Debug overlay at app/src/debug/res/xml/ allows cleartext for 10.0.2.2 only. AndroidManifest references networkSecurityConfig, usesCleartextTraffic absent. |
| `app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java` | 토큰 유무에 따른 Login vs Main 라우팅 | VERIFIED | SessionManager.getInstance().hasToken() → MainActivity (true) or LoginActivity (false). finish() called in both branches. |
| `app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java` | 로그인 성공 시 JWT 저장 + MainActivity 이동 | VERIFIED | sessionManager.saveToken(resource.getData()) called on success. isEmailVerificationRequired() helper present and correctly checks error string — but error string is always "Error: Unauthorized" style (see gap). |
| `app/src/main/java/com/mindJellyProject/mindjelly/users/view/SignUpActivity.java` | createUser() observe + 성공/실패 핸들링 | VERIFIED | userViewModel.createUser(userSaveReqDTO).observe() present. Success: "발송된 이메일을 확인하세요." Toast + LoginActivity navigation. Failure: Korean error message with fallback. |
| `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java` | userId = SessionManager.getUserId() 사용 | VERIFIED | SessionManager.getUserId() used at line 53. Hardcoded 1L absent. Unauthenticated guard present. |
| `app/src/main/java/com/mindJellyProject/mindjelly/users/retrofit/UserRepository.java` | Context 기반 RetrofitClient 사용 | VERIFIED (partial) | Constructor accepts Context, uses RetrofitClient.createService(UserService.class, context). login() error path uses response.message() instead of errorBody() — see gap. |

---

## Key Link Verification

| From | To | Via | Status | Details |
|------|----|-----|--------|---------|
| AuthInterceptor.intercept() | SessionManager.getToken() | SessionManager.getInstance(appContext) 호출 | WIRED | Confirmed in AuthInterceptor.java line 31 |
| RetrofitClient.getInstance(Context) | AuthInterceptor | OkHttpClient.Builder().addInterceptor(new AuthInterceptor(appContext)) | WIRED | Confirmed in RetrofitClient.java lines 29-31 |
| LoginActivity.observeLogin() | SessionManager.saveToken() | Resource.isSuccess() 분기 | WIRED | LoginActivity line 65: sessionManager.saveToken(resource.getData()) |
| SplashActivity | SessionManager.hasToken() | SessionManager.getInstance(this) | WIRED | SplashActivity lines 35-39 |
| SignUpActivity.createUser() observer | LoginActivity | Resource.success() 시 Intent + finish() | WIRED | SignUpActivity lines 85-87 |
| AuthInterceptor.intercept() | 401 응답 감지 | response.code() == 401 체크 후 LoginActivity 인텐트 | WIRED | AuthInterceptor.java lines 42-48 |
| LoginActivity.isEmailVerificationRequired() | UserRepository error body | Resource.getError() 에서 EMAIL_NOT_VERIFIED 검사 | BROKEN | UserRepository.login() posts Resource.error("Error: " + response.message()) — HTTP reason phrase, not response body. EMAIL_NOT_VERIFIED content from server body is never forwarded. |
| All 6 non-user Repositories | RetrofitClient.createService(Class, context) | Context constructor parameter | WIRED | All 7 repositories confirmed using context-aware createService. No deprecated calls remain. |

---

## Data-Flow Trace (Level 4)

| Artifact | Data Variable | Source | Produces Real Data | Status |
|----------|---------------|--------|--------------------|--------|
| LoginActivity | resource (LiveData<Resource<String>>) | UserRepository.login() → UserService.login() Retrofit call | Yes — real HTTP call | FLOWING |
| SplashActivity | hasToken() boolean | SessionManager EncryptedSharedPreferences | Yes — persisted token | FLOWING |
| JellyDrawerActivity | userId (long) | SessionManager.getUserId() → EncryptedSharedPreferences getLong() | Yes — extracted from JWT payload | FLOWING |
| SignUpActivity | resource (LiveData<Resource<Users>>) | UserRepository.createUser() → UserService.createUser() Retrofit call | Yes — real HTTP call | FLOWING |

---

## Behavioral Spot-Checks

Step 7b: SKIPPED — Android app requires device/emulator to run. No runnable CLI entry points.

---

## Probe Execution

Step 7c: No probe scripts found under scripts/*/tests/probe-*.sh. SKIPPED.

---

## Requirements Coverage

| Requirement | Source Plan | Description | Status | Evidence |
|-------------|-------------|-------------|--------|----------|
| AUTH-01 | 01-D | 이메일 + 비밀번호로 회원가입할 수 있다 | SATISFIED | SignUpActivity createUser() observe complete, success Toast + LoginActivity navigation present |
| AUTH-02 | 01-C, 01-D | 이메일 인증 후 계정이 활성화된다 (이메일 인증 UX) | PARTIAL | D-01 (signup email guidance Toast) SATISFIED. D-02 (login email verification message) BLOCKED — UserRepository.login() does not read errorBody(), so EMAIL_NOT_VERIFIED from server never surfaces in LoginActivity |
| AUTH-03 | 01-A, 01-B, 01-C | 로그인 후 앱 재시작해도 세션 유지 | SATISFIED | SessionManager EncryptedSharedPreferences + SplashActivity hasToken() routing verified |
| QUAL-05 | 01-E | userId가 JWT에서 추출되어 사용됨 (하드코딩 1L 제거) | SATISFIED | JellyDrawerActivity uses SessionManager.getUserId(), no 1L literal |

---

## Anti-Patterns Found

| File | Line | Pattern | Severity | Impact |
|------|------|---------|----------|--------|
| UserRepository.java | 216 | `Resource.error("Error: " + response.message())` in login() | Warning | HTTP reason phrase (not response body) forwarded as error — breaks email verification UX detection in LoginActivity |
| RetrofitClient.java | 55-61 | `@Deprecated createService(Class<T>)` overload | Info | Intentional bridge for migration; no callers remain in repositories. Safe to remove in a follow-up cleanup. |

No TBD / FIXME / XXX / HACK markers found in any modified file.

---

## Human Verification Required

None — all automated checks are complete. The identified gap (email verification error path) is verifiable programmatically and documented above.

---

## Gaps Summary

**1 gap blocks full AUTH-02 compliance.**

The email-verification UX branch in `LoginActivity` (`isEmailVerificationRequired()`) is correctly coded but can never trigger because `UserRepository.login()` uses `response.message()` (HTTP status reason phrase) instead of reading the response body via `errorBody().string()`. When a server returns HTTP 4xx with a body containing `"EMAIL_NOT_VERIFIED"`, that string goes unread and `LoginActivity` always shows the generic credential-failure message instead.

**Root cause:** `UserRepository.login()` error path (line 216) uses the same pattern as most other methods in the file: `Resource.error("Error: " + response.message())`. Only `checkDuplicateEmail()` uses `getLoginErrorMessage(response)` which reads the error body. The fix is a one-line change: replace `response.message()` with `getLoginErrorMessage(response)` in the `login()` method.

**Scope:** SC1 partial (AUTH-02 D-02 behavior), Plan C must-have truth 2. All other 3 success criteria are fully verified.

---

_Verified: 2026-05-14T10:00:00Z_
_Verifier: Claude (gsd-verifier)_
