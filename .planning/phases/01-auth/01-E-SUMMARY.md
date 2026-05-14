---
phase: 01-auth
plan: E
subsystem: auth
tags: [retrofit, okhttp, session-manager, context, android-viewmodel, jwt, 401]

requires:
  - phase: 01-A
    provides: SessionManager.getUserId() and SessionManager.clear() for session state
  - phase: 01-B
    provides: RetrofitClient.createService(Class, Context) and RetrofitClient.reset()

provides:
  - All 6 non-user repositories now accept Context and pass it to RetrofitClient
  - All 6 matching ViewModels converted to AndroidViewModel for automatic Application injection
  - JellyDrawerActivity reads userId from SessionManager instead of hardcoded 1L
  - AuthInterceptor detects 401 responses and automatically clears session + navigates to LoginActivity

affects: [phase-2, phase-3, phase-4]

tech-stack:
  added: []
  patterns: [AndroidViewModel pattern for Context-dependent repositories, 401 auto-logout via OkHttp interceptor]

key-files:
  created: []
  modified:
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java
    - app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java
    - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmo/retrofit/AgedEmoRepository.java
    - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmoImage/retrofit/AgedEmoImageRepository.java
    - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/retrofit/BasicEmoRepository.java
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyRepository.java
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombRepository.java
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyImage/retrofit/JellyImageRepository.java
    - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmo/viewmodel/AgedEmoViewModel.java
    - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmoImage/viewmodel/AgedEmoImageViewModel.java
    - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/viewmodel/BasicEmoViewModel.java
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/viewmodel/JellyViewModel.java
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/viewmodel/JellyCombViewModel.java
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyImage/viewmodel/JellyImageViewModel.java

key-decisions:
  - "AndroidViewModel pattern chosen over manual Context passing in Activity — ViewModelProvider auto-injects Application, so Activity code needs no changes"
  - "AuthInterceptor always returns response after 401 handling to satisfy OkHttp chain contract"
  - "JellyDrawerActivity checks getUserId() == -1L (primitive sentinel) not null, matching SessionManager's long return type"

patterns-established:
  - "Repository pattern: constructor accepts Context, passes to RetrofitClient.createService(Class, Context)"
  - "ViewModel pattern: extends AndroidViewModel, receives Application in constructor, passes getApplicationContext() to Repository"
  - "401 auto-logout: OkHttp interceptor clears session + resets Retrofit singleton + starts LoginActivity with FLAG_ACTIVITY_CLEAR_TASK"

requirements-completed:
  - QUAL-05

duration: ~30min (including recovery from partial execution)
completed: 2026-05-14
---

# Plan 01-E Summary

**All 6 repositories wired to context-aware Retrofit, userId hardcoding removed from JellyDrawerActivity, and 401 auto-logout added to AuthInterceptor — Phase 1 auth foundation complete**

## Performance

- **Duration:** ~30 min (includes close-out recovery of previous partial execution)
- **Completed:** 2026-05-14
- **Tasks:** 2
- **Files modified:** 15 (13 Task 1 + 2 Task 2)

## Accomplishments
- 6 Repositories (AgedEmo, AgedEmoImage, BasicEmo, Jelly, JellyComb, JellyImage) updated to accept `Context` and call `RetrofitClient.createService(Class, context)` — eliminates context-free Retrofit calls
- 6 corresponding ViewModels converted from `ViewModel` to `AndroidViewModel` so `ViewModelProvider` auto-injects `Application` context without Activity changes
- `JellyDrawerActivity` replaces hardcoded `userId = 1L` with `SessionManager.getUserId()`, with redirect to `LoginActivity` when unauthenticated (`-1L` sentinel)
- `AuthInterceptor` now detects HTTP 401 → calls `SessionManager.clear()` + `RetrofitClient.reset()` → starts `LoginActivity` with `FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK`

## Task Commits

1. **Task 1: 6 Repository Context + AndroidViewModel conversion** - `b58c353` (feat)
2. **Task 2: JellyDrawerActivity userId fix + AuthInterceptor 401 auto-logout** - `87cb75a` (feat)

## Files Created/Modified
- `JellyDrawerActivity.java` — userId read from SessionManager; unauthenticated redirect
- `AuthInterceptor.java` — 401 detection with session clear + Retrofit reset + LoginActivity navigation
- `AgedEmoRepository.java`, `AgedEmoImageRepository.java`, `BasicEmoRepository.java`, `JellyRepository.java`, `JellyCombRepository.java`, `JellyImageRepository.java` — Context constructor added
- `AgedEmoViewModel.java`, `AgedEmoImageViewModel.java`, `BasicEmoViewModel.java`, `JellyViewModel.java`, `JellyCombViewModel.java`, `JellyImageViewModel.java` — converted to AndroidViewModel

## Decisions Made
- Used `AndroidViewModel` (not manual Context threading) — ViewModelProvider handles Application injection automatically, no Activity call-site changes needed
- `getUserId()` returns primitive `long` with `-1L` sentinel (not nullable `Long`) — `if (userId == -1L)` check is correct per SessionManager contract
- AuthInterceptor always returns the `Response` object even after 401 handling — required by OkHttp `Interceptor` contract

## Deviations from Plan
None — plan executed as specified. Recovery from partial prior execution (Task 1 committed, Task 2 in working tree) handled via close-out path.

## Issues Encountered
Previous execution session committed Task 1 but exited before committing Task 2 or writing SUMMARY.md. Recovery: verified Task 2 changes were correct per plan criteria, committed them, then closed out.

## Next Phase Readiness
- Phase 1 (Auth 기반) is fully complete: SessionManager ✓, AuthInterceptor ✓, RetrofitClient ✓, LoginActivity ✓, SplashActivity ✓, SignUpActivity ✓, userId hardcoding removed ✓, 401 auto-logout ✓
- All authenticated API calls in Phase 2 can use context-aware `RetrofitClient.createService()` — no more context-free Retrofit calls
- **Backend coordination still needed before Phase 2:** JellyDrawerResDTO 감정 필드 추가, startAging API 계약

## Self-Check: PASSED
- `= 1L` in JellyDrawerActivity: 0 occurrences ✓
- `getUserId()` called in JellyDrawerActivity ✓
- `response.code() == 401` in AuthInterceptor ✓
- Context-free `createService(XxxService.class)` in non-User repositories: 0 occurrences ✓

---
*Phase: 01-auth*
*Completed: 2026-05-14*
