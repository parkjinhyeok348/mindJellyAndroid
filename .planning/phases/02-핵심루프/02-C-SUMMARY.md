---
phase: "02-핵심루프"
plan: "C"
subsystem: "adapter-ui"
tags: ["listadapter", "diffutil", "glide", "badge", "listener", "layout"]
dependency_graph:
  requires:
    - "02-A (JellyDrawerResDTO emo1Icon/emo2Icon/status, colors.xml badge_*, strings.xml, bg_status_badge.xml)"
  provides:
    - "item_jelly_drawer.xml (emo1ImageView/emo2ImageView/tvCreateDate/tvAgingStatus/btnStartAging)"
    - "JellyDrawerAdapter ListAdapter (submitList 지원, DiffUtil.ItemCallback)"
    - "JellyDrawerAdapter.OnStartAgingListener 인터페이스 + setOnStartAgingListener()"
    - "3-state 배지 (WAITING/AGING/MATURED) — GradientDrawable mutate 패턴"
    - "Glide emo1/emo2 아이콘 로딩 (serverUrl + 상대경로)"
  affects:
    - "02-D (JellyDrawerActivity — adapter.submitList(), adapter.setOnStartAgingListener() 사용)"
tech_stack:
  added: []
  patterns:
    - "ListAdapter<T, VH> + DiffUtil.ItemCallback — submitList() 기반 리스트 갱신, notifyDataSetChanged 제거"
    - "GradientDrawable mutate+setColor — 공유 drawable 상태 오염 없이 배지 배경색 변경"
    - "OnStartAgingListener 콜백 인터페이스 — Adapter → Activity 이벤트 전달 분리"
key_files:
  created: []
  modified:
    - "app/src/main/res/layout/item_jelly_drawer.xml"
    - "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerAdapter.java"
decisions:
  - "ListAdapter + DiffUtil 채택 — notifyDataSetChanged 대비 부분 갱신 성능 우수, submitList API가 Activity 측 코드를 단순화"
  - "GradientDrawable mutate 패턴 채택 — setBackgroundTintList는 API 21+ 필요, mutate+setColor가 모든 타겟에서 안전"
  - "status null/unknown → WAITING 폴백 — T-02C-02 수용 결정(서버가 API 수준에서 검증), UI 수준 방어는 btnStartAging VISIBLE로 충분"
metrics:
  duration: "약 15분"
  completed_date: "2026-05-14"
  tasks_completed: 2
  files_changed: 2
---

# Phase 02 Plan C: JellyDrawerAdapter ListAdapter 마이그레이션 + 아이콘/배지/버튼 Summary

**한 줄 요약:** item_jelly_drawer.xml 감정 아이콘 2개(emo1/emo2) + 날짜 + 3-state 배지 + 숙성 버튼으로 재구성, JellyDrawerAdapter를 ListAdapter+DiffUtil로 마이그레이션하고 Glide 아이콘 로딩·GradientDrawable mutate 배지·OnStartAgingListener 인터페이스 완성.

---

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| C-1 | item_jelly_drawer.xml 재구성 | 8ace66c | item_jelly_drawer.xml |
| C-2 | JellyDrawerAdapter ListAdapter 마이그레이션 | 2f631a7 | JellyDrawerAdapter.java |

---

## Verification Results

| Check | Result |
|-------|--------|
| `./gradlew compileDebugSources` (C-1 후) | BUILD SUCCESSFUL |
| `./gradlew compileDebugSources` (C-2 후) | BUILD SUCCESSFUL |
| `./gradlew compileDebugSources` (최종) | BUILD SUCCESSFUL |
| item_jelly_drawer.xml — emo1ImageView 존재 | 확인 |
| item_jelly_drawer.xml — emo2ImageView 존재 | 확인 |
| item_jelly_drawer.xml — btnStartAging visibility=gone | 확인 |
| item_jelly_drawer.xml — ivJellyIcon 미존재 | 확인 (0건) |
| item_jelly_drawer.xml — tvJellyId 미존재 | 확인 (0건) |
| item_jelly_drawer.xml — tvAgingStatus bg_status_badge | 확인 |
| JellyDrawerAdapter — extends ListAdapter 선언 | 확인 |
| JellyDrawerAdapter — interface OnStartAgingListener | 확인 |
| JellyDrawerAdapter — setOnStartAgingListener() | 확인 |
| JellyDrawerAdapter — GradientDrawable mutate 패턴 | 확인 |
| JellyDrawerAdapter — setJellyList/notifyDataSetChanged 제거 | 확인 (0건) |
| JellyDrawerAdapter — emo1ImageView/emo2ImageView ViewHolder 필드 | 확인 |

---

## Deviations from Plan

없음 — 플랜 그대로 실행됨.

---

## Known Stubs

없음 — Adapter 계층 구현 완료. Activity 측 연결(submitList 호출, OnStartAgingListener 구현)은 Plan D에서 처리.

---

## Threat Flags

없음 — T-02C-01(btnStartAging WAITING만 VISIBLE), T-02C-02(null→WAITING 폴백 수용) 모두 플랜 threat_model에 등록된 대로 처리됨.

---

## Self-Check: PASSED

| Item | Status |
|------|--------|
| item_jelly_drawer.xml | FOUND |
| JellyDrawerAdapter.java | FOUND |
| 커밋 C-1 (8ace66c) | FOUND |
| 커밋 C-2 (2f631a7) | FOUND |
