---
phase: 01-auth
plan: B
subsystem: auth
tags: [android, jwt, retrofit, okhttp, auth-interceptor]

# Dependency graph
requires:
  - phase: 01-auth-plan-A
    provides: Encrypted JWT token storage through SessionManager.getInstance(Context).getToken()
provides:
  - OkHttp AuthInterceptor that injects Authorization Bearer tokens from SessionManager
  - Context-based RetrofitClient singleton backed by OkHttpClient and AuthInterceptor
  - RetrofitClient.reset() for logout/token-refresh singleton reinitialization
affects: [01-auth, LoginActivity, repositories, 401-handling, userId-removal]

# Tech tracking
tech-stack:
  added: []
  patterns: [application-context network singleton, OkHttp interceptor token injection, transitional Retrofit factory migration]

key-files:
  created:
    - app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java
  modified:
    - app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java

key-decisions:
  - "AuthInterceptor reads SessionManager through an application Context instead of storing or receiving a token directly."
  - "RetrofitClient now exposes Context-based APIs and reset(), while a deprecated no-context createService overload temporarily preserves compilation until downstream repository migration."

patterns-established:
  - "Authenticated Retrofit services should be created with RetrofitClient.createService(Service.class, context)."
  - "Requests without a stored token pass through AuthInterceptor unchanged without an Authorization header."

requirements-completed: [AUTH-03]

# Metrics
duration: 15min
completed: 2026-05-12
---

# Phase 01 Plan B: AuthInterceptor + RetrofitClient OkHttpClient Summary

**Context-backed Retrofit networking now supports automatic JWT Bearer header injection through OkHttp AuthInterceptor.**

## Performance

- **Duration:** 15 min
- **Started:** 2026-05-12T06:50:00Z
- **Completed:** 2026-05-12T07:04:47Z
- **Tasks:** 2 completed
- **Files modified:** 2

## Accomplishments

- Created `AuthInterceptor.java` implementing `okhttp3.Interceptor` with `SessionManager.getInstance(appContext).getToken()` lookup.
- Ensured token absence is safe: null tokens call `chain.proceed(request)` with the original request and no `Authorization` header.
- Refactored `RetrofitClient.java` into a context-based singleton that builds an `OkHttpClient` with `AuthInterceptor`, passes it to Retrofit, and exposes `reset()`.
- Verified targeted Java compilation with `./gradlew.bat :app:compileDebugJavaWithJavac`.

## Task Commits

Each task was committed atomically:

1. **Task 1: AuthInterceptor.java 신규 생성** - `dfb13ce` (feat)
2. **Task 2: RetrofitClient.java 리팩터 — OkHttpClient + AuthInterceptor 적용** - `09603b6` (feat)

**Plan metadata:** recorded by the final docs commit for this summary and tracking updates.

## Files Created/Modified

- `app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java` - Adds OkHttp interceptor that injects `Authorization: Bearer <token>` only when SessionManager has a token.
- `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java` - Converts Retrofit construction to a context-based singleton using OkHttpClient + AuthInterceptor and adds `reset()`.

## Decisions Made

- Used application context in both `AuthInterceptor` and `RetrofitClient` to preserve the existing no-DI Java architecture without retaining Activity contexts.
- Kept BASE_URL unchanged at `http://10.0.2.2:8080/`; the threat register assigns cleartext hardening to Plan C.

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking compile issue] Added temporary deprecated no-context service factory**
- **Found during:** Task 2 (RetrofitClient refactor)
- **Issue:** The plan-required removal of `createService(Class<T>)` caused existing repositories to fail compilation before their downstream context-migration plan runs.
- **Fix:** Added a deprecated `createService(Class<T>)` overload in `RetrofitClient.java` that preserves current compilation while the new `createService(Class<T>, Context)` API is available for downstream plans.
- **Files modified:** `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java`
- **Verification:** `./gradlew.bat :app:compileDebugJavaWithJavac` passed.
- **Committed in:** `09603b6` (part of task commit)

## Deferred Issues

- Existing repositories still call deprecated `RetrofitClient.createService(Class<T>)`, so those call sites will not receive AuthInterceptor until the planned repository Context migration occurs in downstream auth work. The context-based authenticated API is in place for that migration.

## Known Stubs

None. Null-token handling in `AuthInterceptor` is intentional behavior, not a stub.

## Verification

- `grep -n "Bearer" app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java` found the header injection code.
- `grep -n "addInterceptor\|AuthInterceptor\|reset\|Context" app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java` found the required context-based AuthInterceptor wiring and reset support.
- `./gradlew.bat :app:compileDebugJavaWithJavac` completed successfully.

## Self-Check: PASSED

- FOUND: `app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java`
- FOUND: `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java`
- FOUND: task commit `dfb13ce`
- FOUND: task commit `09603b6`
- No tracked file deletions were introduced by the task commits.
