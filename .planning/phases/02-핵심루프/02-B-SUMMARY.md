---
phase: "02-핵심루프"
plan: "B"
subsystem: "ui-layer"
tags: ["layout", "adapter", "viewmodel", "save-flow", "tdd"]
dependency_graph:
  requires:
    - "02-A (JellyViewModel.isLoading, ErrorMessageUtil, strings/colors/drawables)"
    - "02-A (JellyCombService.getJellyCombId 선언)"
  provides:
    - "item_basic_emo.xml — vertical LinearLayout 루트, tvEmoName 레이블"
    - "BasicEmoAdapter — tvEmoName 바인딩, frameLayout.setSelected 선택 상태"
    - "activity_today_jelly.xml — etDiary + btnSave + pbLoading 추가"
    - "JellyCombRepository.getJellyCombId(Long, Long)"
    - "JellyCombViewModel.getJellyCombId(Long, Long)"
    - "TodayJellyActivity — cachedJellyCombId 캐싱, 저장 플로우, JellyDrawerActivity 이동"
  affects:
    - "02-C (JellyDrawerActivity — Wave 2와 무관하게 독립 진행)"
tech_stack:
  added: []
  patterns:
    - "cachedJellyCombId — 감정 선택 시 jellyCombId를 Activity 필드에 캐싱 (Pitfall 1 해결)"
    - "btnSave onClick 내부 observe — Pitfall 2(LiveData 중복 등록) 방지 패턴"
    - "frameLayout.setSelected() — LinearLayout 루트 전환 후 sel_jelly_item 셀렉터 대상 수정"
key_files:
  created: []
  modified:
    - "app/src/main/res/layout/item_basic_emo.xml"
    - "app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/BasicEmoAdapter.java"
    - "app/src/main/res/layout/activity_today_jelly.xml"
    - "app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java"
    - "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombRepository.java"
    - "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/viewmodel/JellyCombViewModel.java"
decisions:
  - "frameLayout.setSelected() 사용 — 루트가 LinearLayout으로 변경되어 sel_jelly_item 셀렉터 배경이 FrameLayout에 있으므로 itemView.setSelected 대신 FrameLayout 참조에 직접 적용"
  - "cachedJellyCombId null 거부 (T-02B-02) — API 호출 전 null 체크로 400 에러 방지"
  - "R 임포트 명시 — TodayJellyActivity는 basicEmo 패키지에 위치하여 자동 임포트가 누락됨, Rule 1 자동 수정"
metrics:
  duration: "약 20분"
  completed_date: "2026-05-14"
  tasks_completed: 2
  files_changed: 6
---

# Phase 02 Plan B: TodayJellyActivity 전체 사용자 플로우 완성 Summary

**한 줄 요약:** item_basic_emo.xml을 vertical LinearLayout으로 재구성하여 감정 이름(tvEmoName) 표시 추가, activity_today_jelly.xml에 etDiary/btnSave/pbLoading 삽입, TodayJellyActivity에 cachedJellyCombId 캐싱 + 빈 일기/null ID 유효성 검증 + createJelly observe + JellyDrawerActivity 이동 완성.

---

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| B-1 | item_basic_emo.xml 재구성 + BasicEmoAdapter tvEmoName 바인딩 | e742cbe | item_basic_emo.xml, BasicEmoAdapter.java |
| B-2 | activity_today_jelly.xml 확장 + TodayJellyActivity 저장 로직 완성 | 1f812d7 | activity_today_jelly.xml, TodayJellyActivity.java, JellyCombRepository.java, JellyCombViewModel.java |

---

## Verification Results

| Check | Result |
|-------|--------|
| `./gradlew compileDebugSources` | BUILD SUCCESSFUL |
| `./gradlew testDebugUnitTest` | BUILD SUCCESSFUL (회귀 없음) |
| item_basic_emo.xml tvEmoName 존재 | 확인 |
| item_basic_emo.xml 루트 LinearLayout | 확인 |
| activity_today_jelly.xml etDiary 존재 | 확인 |
| activity_today_jelly.xml btnSave 존재 | 확인 |
| activity_today_jelly.xml pbLoading 존재 | 확인 |
| emoRecyclerView height_percent=0.5 | 확인 |
| TodayJellyActivity cachedJellyCombId 선언 | 확인 (7회 참조) |
| TodayJellyActivity JellyDrawerActivity 이동 | 확인 |
| TodayJellyActivity error_diary_empty 참조 | 확인 |
| TodayJellyActivity isLoading.observe 연결 | 확인 |

---

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] R 임포트 누락**
- **Found during:** Task B-2 컴파일
- **Issue:** TodayJellyActivity가 `basicEmo` 패키지에 위치하여 `R.string.*` 참조 시 `package R does not exist` 컴파일 오류 발생
- **Fix:** `import com.mindJellyProject.mindjelly.R;` 추가
- **Files modified:** TodayJellyActivity.java
- **Commit:** 1f812d7 (동일 커밋 내 수정)

---

## Requirements Coverage

| Requirement | Status |
|-------------|--------|
| JELLY-01 (감정 이름 + 아이콘 그리드 표시) | 완료 — tvEmoName 바인딩 |
| JELLY-02 (2개 선택 시 합성 프리뷰 즉시 업데이트) | 완료 — 기존 fetchCombinedJellyIcon 유지 |
| JELLY-03 (자유 텍스트 일기 작성) | 완료 — etDiary EditText 추가 |
| JELLY-04 (저장 버튼 클릭 → 젤리 저장) | 완료 — createJelly observe |
| JELLY-05 (저장 완료 후 젤리서랍 이동) | 완료 — JellyDrawerActivity startActivity + finish() |
| QUAL-01 (로딩 인디케이터) | 완료 — isLoading.observe → pbLoading VISIBLE/GONE |
| QUAL-02 (한국어 에러 메시지) | 완료 — ErrorMessageUtil.getKoreanMessage 사용 |

---

## Threat Model Coverage

| Threat ID | Mitigation | Status |
|-----------|-----------|--------|
| T-02B-01 | etDiary trim().isEmpty() 체크 → error_diary_empty Toast | 완료 |
| T-02B-02 | cachedJellyCombId == null 체크 → error_generic Toast | 완료 |
| T-02B-03 | SessionManager.getUserId() == -1L 체크 → error_auth Toast | 완료 |
| T-02B-04 | createJelly 성공/실패 양 분기에서 isLoading.postValue(false) | 완료 |

---

## Known Stubs

없음 — 이 플랜의 모든 기능은 실제 API 호출 경로로 연결되어 있음.
jellyCombId 취득을 위한 `/jellyComb/jelly-comb-id/{firstEmo}/{secondEmo}` 엔드포인트는
백엔드 준비 여부가 미확인 상태이나 (RESEARCH.md Pitfall 1), 클라이언트 코드는 완성됨.
실제 API 미존재 시 cachedJellyCombId가 null로 유지되어 저장 버튼 클릭 시
error_generic Toast가 표시됨 — MVP 수준에서 허용.

---

## Threat Flags

없음 — 신규 네트워크 엔드포인트는 플랜의 threat_model에 등록된 범위 내.

---

## Self-Check: PASSED

| Item | Status |
|------|--------|
| item_basic_emo.xml (tvEmoName, LinearLayout 루트) | FOUND |
| BasicEmoAdapter.java (tvEmoName 필드, frameLayout 필드) | FOUND |
| activity_today_jelly.xml (etDiary, btnSave, pbLoading) | FOUND |
| TodayJellyActivity.java (cachedJellyCombId, isLoading.observe, JellyDrawerActivity) | FOUND |
| JellyCombRepository.java (getJellyCombId 메서드) | FOUND |
| JellyCombViewModel.java (getJellyCombId 메서드) | FOUND |
| 02-B-SUMMARY.md | FOUND |
| 커밋 2개 (e742cbe, 1f812d7) | 2/2 확인됨 |
