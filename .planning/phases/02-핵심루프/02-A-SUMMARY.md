---
phase: "02-핵심루프"
plan: "A"
subsystem: "data-layer"
tags: ["dto", "retrofit", "viewmodel", "resources", "tdd"]
dependency_graph:
  requires: []
  provides:
    - "JellyDrawerResDTO (emo1Name/emo1Icon/emo2Name/emo2Icon/status 필드)"
    - "JellyStartAgingReqDTO (status=AGING 고정)"
    - "JellyService.startAging PATCH /api/jelly/{jellyId}"
    - "JellyRepository.startAging(Long, JellyStartAgingReqDTO)"
    - "JellyViewModel.isLoading MutableLiveData<Boolean>"
    - "JellyViewModel.startAging(Long)"
    - "JellyCombService.getJellyCombId(Long, Long)"
    - "ErrorMessageUtil.getKoreanMessage(Resource, Context)"
    - "colors.xml 13개 신규 색상"
    - "strings.xml 21개 신규 한국어 문자열"
    - "bg_edit_text.xml / bg_status_badge.xml drawable"
  affects:
    - "02-B (JellyDrawerActivity — JellyDrawerResDTO, startAging, ErrorMessageUtil 사용)"
    - "02-C (TodayJellyActivity — isLoading, ErrorMessageUtil, strings 사용)"
tech_stack:
  added: []
  patterns:
    - "MutableLiveData<Boolean> isLoading — Resource<T>에 LOADING 추가 없이 별도 필드로 로딩 상태 관리"
    - "JellyStartAgingReqDTO setter 미노출 — T-02A-01 Tampering mitigation"
    - "ErrorMessageUtil 정적 유틸 — HTTP 코드 기반 한국어 에러 분기"
key_files:
  created:
    - "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyStartAgingReqDTO.java"
    - "app/src/main/java/com/mindJellyProject/mindjelly/common/ErrorMessageUtil.java"
    - "app/src/main/res/drawable/bg_edit_text.xml"
    - "app/src/main/res/drawable/bg_status_badge.xml"
    - "app/src/test/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyDrawerResDTOTest.java"
  modified:
    - "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyDrawerResDTO.java"
    - "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyService.java"
    - "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyRepository.java"
    - "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/viewmodel/JellyViewModel.java"
    - "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombService.java"
    - "app/src/main/res/values/colors.xml"
    - "app/src/main/res/values/strings.xml"
decisions:
  - "isLoading을 Resource<T>에 추가하지 않고 별도 MutableLiveData<Boolean>으로 구현 — 기존 observe 코드 파괴 없음 (QUAL-01, RESEARCH.md Pattern 2)"
  - "JellyStartAgingReqDTO.status setter 미노출 — 클라이언트 임의 상태 전환 차단 (T-02A-01)"
  - "ErrorMessageUtil 정적 유틸 신규 생성 — 각 Activity 내 중복 switch 방지 (QUAL-02)"
  - "getJellyCombId 엔드포인트 선언 + TODO 주석 — 백엔드 존재 여부 미확인, 호출 전 확인 필요 (Pitfall 1)"
metrics:
  duration: "약 25분"
  completed_date: "2026-05-14"
  tasks_completed: 3
  files_changed: 11
---

# Phase 02 Plan A: 데이터 계층 + 공통 리소스 기반 구축 Summary

**한 줄 요약:** Gson @SerializedName 필드 확장(DTO 5개), PATCH startAging API 계층 3단(Service/Repository/ViewModel), isLoading LiveData, ErrorMessageUtil 401/404/5xx/network 분기, colors 13개 + strings 21개 + drawable 2개 리소스 정의 완료.

---

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| RED | TDD 실패 테스트 작성 | 5e406c6 | JellyDrawerResDTOTest.java |
| A-1 | 데이터 모델 확장 | 49a0f0c | JellyDrawerResDTO.java, JellyStartAgingReqDTO.java |
| A-2 | API 계층 확장 | bef75c3 | JellyService.java, JellyRepository.java, JellyViewModel.java, JellyCombService.java |
| A-3 | 공통 리소스 | f432225 | ErrorMessageUtil.java, colors.xml, strings.xml, bg_edit_text.xml, bg_status_badge.xml |

---

## Verification Results

| Check | Result |
|-------|--------|
| `./gradlew compileDebugSources` | BUILD SUCCESSFUL |
| `./gradlew testDebugUnitTest` | BUILD SUCCESSFUL (기존 테스트 회귀 없음) |
| JellyDrawerResDTO emo1Icon 필드 존재 | 확인 |
| JellyViewModel isLoading 선언 존재 | 확인 |
| colors.xml 항목 수 | 15개 (기존 2 + 신규 13) |
| strings.xml 항목 수 | 33개 (기존 11 + 신규 22) |
| bg_edit_text.xml radius=8dp | 확인 |
| bg_status_badge.xml radius=4dp | 확인 |
| ErrorMessageUtil 존재 | 확인 |

---

## Deviations from Plan

### Auto-fixed Issues

없음 — 플랜 그대로 실행됨.

### Rule 2 (Missing Critical Functionality) — 자동 적용

**1. [Rule 2 - Security] JellyStartAgingReqDTO setter 미노출**
- **Found during:** Task A-1
- **Issue:** T-02A-01 Threat: 클라이언트가 임의 상태(예: "MATURED")로 PATCH 가능
- **Fix:** `status` 필드를 `final`로 선언하고 setter를 제공하지 않음
- **Files modified:** JellyStartAgingReqDTO.java
- **Commit:** 49a0f0c

---

## TDD Gate Compliance

| Gate | Commit | Status |
|------|--------|--------|
| RED (test) | 5e406c6 | 컴파일 실패 확인 (JellyStartAgingReqDTO 미존재) |
| GREEN (feat) | 49a0f0c | 컴파일 성공 + testDebugUnitTest PASS |
| REFACTOR | — | 불필요 (코드 구조 충분히 명확) |

---

## Known Stubs

없음 — 이 플랜은 데이터 계층 및 리소스 정의만 담당. UI 연결은 Wave 2 플랜(B, C)에서 처리.

---

## Threat Flags

없음 — 플랜의 threat_model에 등록된 T-02A-01/02/03 모두 처리됨.

---

## Self-Check: PASSED

| Item | Status |
|------|--------|
| JellyStartAgingReqDTO.java | FOUND |
| JellyDrawerResDTO.java | FOUND |
| JellyService.java | FOUND |
| JellyViewModel.java | FOUND |
| ErrorMessageUtil.java | FOUND |
| bg_edit_text.xml | FOUND |
| bg_status_badge.xml | FOUND |
| 02-A-SUMMARY.md | FOUND |
| 커밋 4개 (5e406c6, 49a0f0c, bef75c3, f432225) | 4/4 확인됨 |
