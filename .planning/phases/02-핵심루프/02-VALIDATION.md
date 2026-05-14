---
phase: 02
slug: 핵심루프
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-05-14
---

# Phase 02 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 4.13.2 + Espresso 3.5.1 (의존성 이미 존재) |
| **Config file** | 기본 Android 프로젝트 구조 — Wave 0 설정 불필요 |
| **Quick run command** | `./gradlew compileDebugSources` |
| **Full suite command** | `./gradlew testDebugUnitTest` |
| **Estimated runtime** | ~30 seconds (unit tests only) |

---

## Sampling Rate

- **After every task commit:** Run `./gradlew compileDebugSources` (컴파일 오류 없음 확인)
- **After every plan wave:** Run `./gradlew testDebugUnitTest`
- **Before `/gsd-verify-work`:** 빌드 성공 + 에뮬레이터 수동 E2E 플로우 확인 (저장 → 서랍 이동)
- **Max feedback latency:** ~30 seconds (unit), manual for E2E

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|-------------|--------|
| 02-A-01 | A | 1 | JELLY-01 | — | 감정 선택 2개 제한 (3개째 선택 불가) | Unit (Adapter) | `./gradlew testDebugUnitTest` | ❌ Wave 0 | ⬜ pending |
| 02-A-02 | A | 1 | JELLY-02 | — | 2개 선택 시 프리뷰 API 즉시 호출 | Unit (ViewModel) | `./gradlew testDebugUnitTest` | ❌ Wave 0 | ⬜ pending |
| 02-B-01 | B | 1 | JELLY-03/04 | T-V5 | 빈 일기 저장 거부 (isEmpty check) | Unit | `./gradlew testDebugUnitTest` | ❌ Wave 0 | ⬜ pending |
| 02-B-02 | B | 1 | JELLY-05 | — | 저장 성공 후 JellyDrawerActivity 이동 | Manual (에뮬레이터) | — | Manual only | ⬜ pending |
| 02-C-01 | C | 2 | DRAW-01/02 | — | 날짜 + 아이콘 2개 + 상태 배지 표시 | Manual (에뮬레이터) | — | Manual only | ⬜ pending |
| 02-C-02 | C | 2 | DRAW-03 | — | 숙성시키기 → PATCH 호출 + AgingRoom 이동 | Manual (실 API) | — | Manual only | ⬜ pending |
| 02-C-03 | C | 2 | DRAW-04 | — | 빈 상태 안내 표시 | Unit | `./gradlew testDebugUnitTest` | ❌ Wave 0 | ⬜ pending |
| 02-D-01 | D | 2 | QUAL-01 | — | 네트워크 요청 중 ProgressBar VISIBLE | Unit (isLoading LiveData) | `./gradlew testDebugUnitTest` | ❌ Wave 0 | ⬜ pending |
| 02-D-02 | D | 2 | QUAL-02 | — | 에러 시 한국어 Toast 표시 | Unit (ErrorMessageUtil) | `./gradlew testDebugUnitTest` | ❌ Wave 0 | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

- 현재 `src/test/` 에 프로젝트 전용 테스트 파일 없음 — 기본 Android 구조 사용
- `BasicEmoAdapterTest.java` — 2개 선택 제한 로직 단위 테스트
- `ErrorMessageUtilTest.java` — HTTP 에러코드 → 한국어 메시지 변환 단위 테스트

*Wave 0이 선행 필요한 태스크는 ❌ Wave 0으로 표시.*

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| 저장 성공 후 JellyDrawerActivity 이동 | JELLY-05 | 실 백엔드 서버 필요 | 1) 에뮬레이터 실행, 2) 감정 2개 선택 + 일기 작성 + 저장, 3) JellyDrawerActivity로 이동 확인 |
| 날짜·아이콘·상태 배지 목록 표시 | DRAW-01/02 | 실 데이터 필요 | JellyDrawerActivity에서 과거 기록 목록 및 배지 표시 확인 |
| 숙성시키기 → AgingRoom 이동 | DRAW-03 | 실 PATCH API 필요 | "숙성시키기" 버튼 클릭 후 AgingRoomActivity 이동 확인 |

---

## Validation Sign-Off

- [ ] All tasks have `<automated>` verify or Wave 0 dependencies
- [ ] Sampling continuity: no 3 consecutive tasks without automated verify
- [ ] Wave 0 covers all MISSING references
- [ ] No watch-mode flags
- [ ] Feedback latency < 30s (unit), manual for E2E
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending
