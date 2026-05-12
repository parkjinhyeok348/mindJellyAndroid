---
phase: 01-auth
plan: C
subsystem: auth
tags: [android, jwt, session, splash, network-security-config]

# Dependency graph
requires:
  - phase: 01-auth-plan-A
    provides: SessionManager encrypted JWT storage and hasToken routing API
  - phase: 01-auth-plan-B
    provides: Context-based RetrofitClient with AuthInterceptor
provides:
  - LoginActivity JWT persistence through SessionManager.saveToken()
  - Korean login error split for email verification versus credential failure
  - SplashActivity session-token routing to MainActivity or LoginActivity
  - Debug-only emulator HTTP cleartext allowance with release-safe default denial
affects: [01-auth, LoginActivity, SplashActivity, UserRepository, network-security]

# Tech tracking
tech-stack:
  added: []
  patterns: [AndroidViewModel application-context repository construction, debug resource overlay for network security]

key-files:
  created:
    - app/src/main/res/xml/network_security_config.xml
    - app/src/debug/res/xml/network_security_config.xml
  modified:
    - app/src/main/AndroidManifest.xml
    - app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java
    - app/src/main/java/com/mindJellyProject/mindjelly/users/retrofit/UserRepository.java
    - app/src/main/java/com/mindJellyProject/mindjelly/users/viewmodel/UserViewModel.java
    - app/src/main/java/com/mindJellyProject/mindjelly/users/view/SignUpActivity.java
    - app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java

key-decisions:
  - "UserRepository now requires Context and is constructed through AndroidViewModel so RetrofitClient can use the authenticated Context-based service path."
  - "Debug-only cleartext uses an app/src/debug resource overlay; the main resource denies cleartext by default for release safety."

patterns-established:
  - "Login success stores JWT in SessionManager before navigation and never logs the token."
  - "Splash routing checks only local token presence; token expiry remains deferred to authenticated 401 handling."

requirements-completed: [AUTH-02, AUTH-03]

# Metrics
duration: 25min
completed: 2026-05-12
---

# Phase 01 Plan C: Login Session Routing and Debug HTTP Summary

**Login now persists JWT sessions, surfaces Korean auth-specific errors, routes app startup by stored token, and limits emulator HTTP cleartext to debug builds.**

## Performance

- **Duration:** 25 min
- **Started:** 2026-05-12T06:52:00Z
- **Completed:** 2026-05-12T07:17:07Z
- **Tasks:** 3 completed
- **Files modified:** 8

## Accomplishments

- Added network security configuration wired from `AndroidManifest.xml`, removing global `usesCleartextTraffic` and restricting `10.0.2.2` HTTP to a debug resource overlay.
- Updated `LoginActivity` to initialize `SessionManager`, save successful JWT login responses, and distinguish `EMAIL_NOT_VERIFIED`/이메일 인증 failures from generic credential failures.
- Migrated `UserRepository` to the Context-based `RetrofitClient.createService(..., context)` path through `UserViewModel`'s application context.
- Updated `SplashActivity` to route users with stored tokens to `MainActivity` and users without tokens to `LoginActivity` after the existing splash delay.
- Verified the plan with `./gradlew.bat :app:compileDebugJavaWithJavac`.

## Task Commits

Each task was committed atomically:

1. **Task 1: network_security_config.xml 생성 + AndroidManifest 업데이트** - `edc9011` (feat)
2. **Task 2: LoginActivity JWT 저장 + 에러 메시지 세분화** - `e6f74ab` (feat)
3. **Task 3: SplashActivity 세션 기반 라우팅** - `eae5bde` (feat)

**Plan metadata:** committed separately after this summary and tracking updates.

## Files Created/Modified

- `app/src/main/res/xml/network_security_config.xml` - Release-safe default network security config with cleartext disabled.
- `app/src/debug/res/xml/network_security_config.xml` - Debug overlay allowing cleartext only for emulator host `10.0.2.2`.
- `app/src/main/AndroidManifest.xml` - Uses `@xml/network_security_config` and no longer enables global cleartext traffic.
- `app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java` - Saves JWT tokens and shows Korean email-verification or credential-failure messages.
- `app/src/main/java/com/mindJellyProject/mindjelly/users/retrofit/UserRepository.java` - Uses Context-based Retrofit service creation and preserves login error body details for UI branching.
- `app/src/main/java/com/mindJellyProject/mindjelly/users/viewmodel/UserViewModel.java` - Uses `AndroidViewModel` so repositories receive application context without leaking Activities.
- `app/src/main/java/com/mindJellyProject/mindjelly/users/view/SignUpActivity.java` - Uses `ViewModelProvider` after the ViewModel constructor migration so compile remains green.
- `app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java` - Routes by `SessionManager.hasToken()`.

## Decisions Made

- Used a debug resource overlay for cleartext because Android `debug-overrides` cannot make a domain-config cleartext-only in release; the main resource denies cleartext by default.
- Kept token validation local in `SplashActivity` per the plan: presence routes to `MainActivity`, expiry is deferred to future 401 handling.
- Migrated `UserViewModel` to `AndroidViewModel` rather than passing Activity contexts into repositories.

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 2 - Critical Security] Implemented debug-only cleartext with a resource overlay**
- **Found during:** Task 1 (network security config)
- **Issue:** A single main `network_security_config.xml` cannot safely allow cleartext only for debug while denying it in release.
- **Fix:** Added `app/src/debug/res/xml/network_security_config.xml` for `10.0.2.2` cleartext and kept main `app/src/main/res/xml/network_security_config.xml` cleartext-denied.
- **Files modified:** `app/src/debug/res/xml/network_security_config.xml`, `app/src/main/res/xml/network_security_config.xml`, `app/src/main/AndroidManifest.xml`
- **Verification:** Manifest has one `networkSecurityConfig`, zero `usesCleartextTraffic`; Gradle debug Java compile passed.
- **Committed in:** `edc9011`

**2. [Rule 3 - Blocking Compile] Updated ViewModel construction after UserRepository Context migration**
- **Found during:** Task 2 (LoginActivity/UserRepository migration)
- **Issue:** Changing `UserRepository` to require `Context` meant `UserViewModel` and `SignUpActivity` no longer compiled with no-arg construction.
- **Fix:** Migrated `UserViewModel` to `AndroidViewModel` and updated `SignUpActivity` to use `ViewModelProvider`.
- **Files modified:** `app/src/main/java/com/mindJellyProject/mindjelly/users/viewmodel/UserViewModel.java`, `app/src/main/java/com/mindJellyProject/mindjelly/users/view/SignUpActivity.java`
- **Verification:** `./gradlew.bat :app:compileDebugJavaWithJavac` passed.
- **Committed in:** `e6f74ab`

**3. [Rule 1 - Bug] Stopped empty login form from still sending a login request**
- **Found during:** Task 2 (LoginActivity login observer update)
- **Issue:** The existing empty-field Toast did not return, so empty credentials still triggered a login request.
- **Fix:** Added `return` after the required-input Toast.
- **Files modified:** `app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java`
- **Verification:** Java compile passed and login code contains the early return.
- **Committed in:** `e6f74ab`

## Auth Gates

None.

## Known Stubs

None found in created/modified Plan 01-C files that block this plan's goal.

## Verification

- `network_config_exists=True`
- `debug_network_config_exists=True`
- `usesCleartextTraffic=0`
- `networkSecurityConfig=1`
- `saveToken=1`
- `emailVerifyMsg=1`
- `hasToken=1`
- `./gradlew.bat :app:compileDebugJavaWithJavac` — **BUILD SUCCESSFUL**

## Threat Flags

None — modified trust boundaries match the plan threat model.

## Deferred Issues

None.

## Self-Check: PASSED

- Created files exist: `app/src/main/res/xml/network_security_config.xml`, `app/src/debug/res/xml/network_security_config.xml`
- Required code markers exist: `saveToken`, `이메일 인증이 필요`, `hasToken`, `networkSecurityConfig`
- Task commits exist: `edc9011`, `e6f74ab`, `eae5bde`
- Targeted compile passed after final task.
