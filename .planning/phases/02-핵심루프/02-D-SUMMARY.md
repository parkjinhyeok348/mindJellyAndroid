---
phase: "02-핵심루프"
plan: "D"
subsystem: "activity-ui"
tags: ["viewbinding", "empty-state", "loading", "startaging", "error-korean", "draw", "qual"]
dependency_graph:
  requires:
    - "02-A (JellyViewModel.isLoading, ErrorMessageUtil, strings/colors/drawables)"
    - "02-C (JellyDrawerAdapter.OnStartAgingListener, submitList)"
  provides:
    - "activity_jelly_drawer.xml — FrameLayout 루트(id=main), rvJellyList/tvEmptyState/tvEmptyStateBody/pbLoading"
    - "JellyDrawerActivity — ActivityJellyDrawerBinding, 빈 상태, isLoading, startAging, 한국어 에러"
  affects:
    - "Phase 3 (AgingRoomActivity — startAging 성공 후 이동 진입점)"
tech_stack:
  added: []
  patterns:
    - "ViewBinding(ActivityJellyDrawerBinding) — setContentView(binding.getRoot()), binding.* 접근"
    - "FrameLayout 루트 + GONE/VISIBLE state machine — Loading/Loaded empty/Loaded non-empty/Error 4개 상태"
    - "isLoading.postValue(true/false) — getJellyList/startAging 양 분기에서 명시 해제 (T-02D-03)"
    - "adapter.setOnStartAgingListener 콜백 — Adapter→Activity 이벤트 전달"
key_files:
  created: []
  modified:
    - "app/src/main/res/layout/activity_jelly_drawer.xml"
    - "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java"
decisions:
  - "ViewBinding 도입 — findViewById 체이닝 제거, 타입 안전 접근, Plan B TodayJellyActivity 패턴 일관성"
  - "isLoading.postValue(false) 양 분기 명시 — T-02D-03(isLoading 미해제) 방어, 성공/에러 모두 false 보장"
  - "userId long 타입 사용 — SessionManager.getUserId() 반환 타입이 long(primitive), Long autoboxing 제거"
metrics:
  duration: "약 15분"
  completed_date: "2026-05-14"
  tasks_completed: 2
  files_changed: 2
---

# Phase 02 Plan D: JellyDrawerActivity ViewBinding 전환 + 빈 상태 + 숙성 콜백 완성 Summary

**한 줄 요약:** activity_jelly_drawer.xml을 FrameLayout 루트로 재구성하여 빈 상태 뷰(tvEmptyState/tvEmptyStateBody)와 로딩 인디케이터(pbLoading)를 추가하고, JellyDrawerActivity를 ViewBinding으로 전환하여 isLoading 토글·빈 상태 분기·startAging→AgingRoom 이동·한국어 에러 Toast를 완성했다.

---

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| D-1 | activity_jelly_drawer.xml FrameLayout 재구성 — 빈 상태 뷰 + pbLoading | efe0451 | activity_jelly_drawer.xml, JellyDrawerActivity.java(임시 Rule 3) |
| D-2 | JellyDrawerActivity ViewBinding 전환 + 빈 상태 + isLoading + startAging + 한국어 에러 | e3036f4 | JellyDrawerActivity.java |

---

## Verification Results

| Check | Result |
|-------|--------|
| `./gradlew compileDebugSources` (D-1 후) | BUILD SUCCESSFUL |
| `./gradlew compileDebugSources` (D-2 후) | BUILD SUCCESSFUL |
| activity_jelly_drawer.xml FrameLayout 루트 | 확인 |
| activity_jelly_drawer.xml tvEmptyState 존재 | 확인 |
| activity_jelly_drawer.xml tvEmptyStateBody 존재 | 확인 |
| activity_jelly_drawer.xml pbLoading 존재 | 확인 |
| activity_jelly_drawer.xml rvJellyList marginTop=273dp 보존 | 확인 |
| activity_jelly_drawer.xml basic_background 루트 배경 | 확인 |
| JellyDrawerActivity ActivityJellyDrawerBinding 참조 | 확인 (3건) |
| JellyDrawerActivity isLoading.observe 호출 | 확인 (2건) |
| JellyDrawerActivity isLoading.postValue(true) 호출 | 확인 (1건) |
| JellyDrawerActivity tvEmptyState 참조 | 확인 (6건) |
| JellyDrawerActivity submitList 호출 | 확인 (1건) |
| JellyDrawerActivity setOnStartAgingListener 호출 | 확인 (1건) |
| JellyDrawerActivity AgingRoomActivity 이동 코드 | 확인 (2건) |
| JellyDrawerActivity ErrorMessageUtil.getKoreanMessage 호출 | 확인 (2건) |
| JellyDrawerActivity 하드코딩 영문 에러 문자열 없음 | 확인 (0건) |

---

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] setJellyList() → submitList() 임시 수정 (D-1 컴파일 블로킹)**
- **Found during:** Task D-1 컴파일 검증
- **Issue:** Plan C에서 JellyDrawerAdapter가 ListAdapter로 마이그레이션되면서 setJellyList() 메서드가 제거됨. 기존 JellyDrawerActivity가 setJellyList()를 호출하고 있어 컴파일 오류 발생
- **Fix:** D-1 커밋에서 adapter.setJellyList() → adapter.submitList()로 임시 교체. D-2에서 JellyDrawerActivity 전체 재작성으로 완전히 해소됨
- **Files modified:** JellyDrawerActivity.java
- **Commit:** efe0451 (D-1 커밋 내 포함)

**2. [Rule 3 - Blocking] 워크트리 빌드 환경 설정 — local.properties 복사**
- **Found during:** Task D-1 컴파일 검증
- **Issue:** 워크트리(`agent-adf7449607be661d6`) 경로에 local.properties가 없어 SDK 경로 미인식으로 BUILD FAILED
- **Fix:** main 레포의 local.properties를 워크트리 루트에 복사하여 `sdk.dir` 경로 제공
- **Files modified:** (local.properties — .gitignore 대상, 커밋 미포함)
- **Commit:** N/A

---

## Requirements Coverage

| Requirement | Status |
|-------------|--------|
| DRAW-03 (숙성시키기 → AgingRoom 이동) | 완료 — setOnStartAgingListener + startAging + AgingRoomActivity 이동 |
| DRAW-04 (빈 상태 안내 표시) | 완료 — tvEmptyState/tvEmptyStateBody VISIBLE (리스트 empty 분기) |
| QUAL-01 (로딩 인디케이터) | 완료 — isLoading.observe → pbLoading VISIBLE/GONE |
| QUAL-02 (한국어 에러 메시지) | 완료 — ErrorMessageUtil.getKoreanMessage (하드코딩 제거) |

---

## Threat Model Coverage

| Threat ID | Mitigation | Status |
|-----------|-----------|--------|
| T-02D-01 | userId == -1L → error_auth Toast + LoginActivity 리다이렉트 | 완료 |
| T-02D-02 | startAging jellyId — Adapter에서 받은 값 그대로 전달, 서버 검증 의존 | 수용 (accept) |
| T-02D-03 | getJellyList/startAging 성공·에러 양 분기에서 isLoading.postValue(false) 명시 | 완료 |

---

## Known Stubs

없음 — 이 플랜의 모든 기능은 실제 API 호출 경로로 연결되어 있음.
AgingRoomActivity는 스텁 Activity이지만 이동 자체는 완성됨 (Phase 3 범위).

---

## Threat Flags

없음 — 신규 네트워크 엔드포인트 없음. 모든 보안 경계는 플랜 threat_model 등록 범위 내.

---

## Self-Check: PASSED

| Item | Status |
|------|--------|
| activity_jelly_drawer.xml (FrameLayout, tvEmptyState, tvEmptyStateBody, pbLoading, 273dp) | FOUND |
| JellyDrawerActivity.java (ActivityJellyDrawerBinding, isLoading.observe, isLoading.postValue, tvEmptyState, submitList, setOnStartAgingListener, AgingRoomActivity, ErrorMessageUtil) | FOUND |
| 02-D-SUMMARY.md | FOUND |
| 커밋 2개 (efe0451, e3036f4) | 2/2 확인됨 |
