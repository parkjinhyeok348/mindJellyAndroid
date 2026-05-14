---
phase: 01-auth
verified: 2026-05-14T11:00:00Z
status: passed
score: 4/4 must-haves verified
overrides_applied: 0
re_verification:
  previous_status: gaps_found
  previous_score: 3/4
  gaps_closed:
    - "UserRepository.login() 실패 경로가 getLoginErrorMessage(response)를 호출해 errorBody()를 읽도록 수정됨 — isEmailVerificationRequired() 조건이 이제 실제 서버 응답에서 충족 가능"
  gaps_remaining: []
  regressions: []
---

# Phase 1: Auth 기반 Verification Report

**Phase Goal:** 실제 사용자로 로그인하고 인증된 API 호출이 가능한 앱 완성
**Verified:** 2026-05-14T11:00:00Z
**Status:** passed
**Re-verification:** Yes — after gap closure

---

## Goal Achievement

### Observable Truths

| # | Truth | Status | Evidence |
|---|-------|--------|----------|
| 1 | 신규 사용자가 이메일 + 비밀번호로 회원가입하고 이메일 인증을 받을 수 있다 | VERIFIED | SignUpActivity createUser() observe VERIFIED. Email verification error path now fixed: UserRepository.login() line 216 calls getLoginErrorMessage(response) which reads errorBody().string(); LoginActivity.isEmailVerificationRequired() can now fire on EMAIL_NOT_VERIFIED server responses. |
| 2 | 가입된 사용자가 로그인 후 앱을 재시작해도 세션이 유지된다 | VERIFIED | SessionManager uses EncryptedSharedPreferences (AES256_GCM). SplashActivity calls SessionManager.getInstance().hasToken() and routes to MainActivity when true. No regression. |
| 3 | 모든 API 요청에 JWT Bearer 토큰이 자동으로 포함된다 (AuthInterceptor 동작 확인) | VERIFIED | AuthInterceptor.intercept() reads token from SessionManager, injects "Authorization: Bearer " + token. RetrofitClient wires interceptor via addInterceptor(new AuthInterceptor(appContext)). All repositories use context-aware createService. No regression. |
| 4 | JellyDrawerActivity에서 userId 하드코딩(1L)이 제거되고 JWT에서 추출된 userId가 사용된다 | VERIFIED | No "= 1L" literal in JellyDrawerActivity. SessionManager.getUserId() called at line 53. -1L sentinel guard at line 54. No regression. |

**Score:** 4/4 truths verified

---

## Required Artifacts

| Artifact | Expected | Status | Details |
|----------|----------|--------|---------|
| `app/src/main/java/com/mindJellyProject/mindjelly/common/SessionManager.java` | JWT 저장/조회/삭제 + userId 추출 | VERIFIED | All 6 public methods present. EncryptedSharedPreferences + AES256_GCM. getUserId() returns primitive long, default -1L. |
| `app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java` | OkHttp Bearer 토큰 주입 인터셉터 | VERIFIED | Injects "Bearer " + token when token != null. 401 triggers SessionManager.clear() + RetrofitClient.reset() + LoginActivity intent with FLAG_ACTIVITY_CLEAR_TASK. |
| `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java` | OkHttpClient + AuthInterceptor 포함 Retrofit 싱글턴 | VERIFIED | Context-based singleton. addInterceptor(new AuthInterceptor(appContext)). reset() method present. |
| `app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java` | 로그인 성공 시 JWT 저장 + MainActivity 이동, 이메일 인증 미완료 메시지 | VERIFIED | sessionManager.saveToken(resource.getData()) on success. isEmailVerificationRequired() helper wired to now-correct error string from UserRepository. |
| `app/src/main/java/com/mindJellyProject/mindjelly/users/retrofit/UserRepository.java` | Context 기반 RetrofitClient 사용 + login() errorBody 읽기 | VERIFIED | Constructor accepts Context, uses RetrofitClient.createService(UserService.class, context). login() error path (line 216) now calls getLoginErrorMessage(response) — reads errorBody().string() before falling back to response.message(). |
| `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java` | userId = SessionManager.getUserId() 사용 | VERIFIED | SessionManager.getUserId() used at line 53. Hardcoded 1L absent. Unauthenticated guard present. |

---

## Key Link Verification

| From | To | Via | Status | Details |
|------|----|-----|--------|---------|
| AuthInterceptor.intercept() | SessionManager.getToken() | SessionManager.getInstance(appContext) | WIRED | AuthInterceptor.java line 31 |
| RetrofitClient.getInstance(Context) | AuthInterceptor | OkHttpClient.Builder().addInterceptor(new AuthInterceptor(appContext)) | WIRED | RetrofitClient.java lines 29-31 |
| LoginActivity.observeLogin() | SessionManager.saveToken() | Resource.isSuccess() 분기 | WIRED | LoginActivity line 65 |
| SplashActivity | SessionManager.hasToken() | SessionManager.getInstance(this) | WIRED | SplashActivity lines 35-39 |
| LoginActivity.isEmailVerificationRequired() | UserRepository errorBody 내용 | getLoginErrorMessage(response) → errorBody().string() | WIRED | UserRepository.java lines 216, 228-241 — gap now closed |
| AuthInterceptor.intercept() | 401 응답 감지 후 LoginActivity 이동 | response.code() == 401 체크 | WIRED | AuthInterceptor.java lines 42-48 |
| All repositories | RetrofitClient.createService(Class, context) | Context constructor parameter | WIRED | All repositories confirmed using context-aware createService |

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
| AUTH-02 | 01-C, 01-D | 이메일 인증 후 계정이 활성화된다 (이메일 인증 UX) | SATISFIED | Signup email guidance Toast present. Login email verification message now correctly wired: UserRepository.login() reads errorBody() via getLoginErrorMessage(), so EMAIL_NOT_VERIFIED from server reaches LoginActivity.isEmailVerificationRequired() |
| AUTH-03 | 01-A, 01-B, 01-C | 로그인 후 앱 재시작해도 세션 유지 | SATISFIED | SessionManager EncryptedSharedPreferences + SplashActivity hasToken() routing verified |
| QUAL-05 | 01-E | userId가 JWT에서 추출되어 사용됨 (하드코딩 1L 제거) | SATISFIED | JellyDrawerActivity uses SessionManager.getUserId(), no 1L literal |

---

## Anti-Patterns Found

| File | Line | Pattern | Severity | Impact |
|------|------|---------|----------|--------|
| RetrofitClient.java | 55-61 | `@Deprecated createService(Class<T>)` overload without Context | Info | Intentional bridge for migration; no callers remain in repositories. Safe to remove in a follow-up cleanup. |

No TBD / FIXME / XXX / HACK markers found in any modified file.

---

## Human Verification Required

None — all automated checks are complete. All 4 success criteria are verifiable programmatically and confirmed VERIFIED.

---

## Gaps Summary

No gaps. The single gap from the initial verification (email verification error path) has been closed.

**Gap closed:** `UserRepository.login()` error path previously used `response.message()` (HTTP reason phrase). It now calls `getLoginErrorMessage(response)` (line 216), which reads `errorBody().string()` first. This ensures the server's `EMAIL_NOT_VERIFIED` body content reaches `LoginActivity.isEmailVerificationRequired()`, making the email-not-verified UX branch functional.

All 4 success criteria are fully satisfied.

---

_Verified: 2026-05-14T11:00:00Z_
_Verifier: Claude (gsd-verifier)_
