# Phase 2: 핵심 루프 - Context

**Gathered:** 2026-05-14
**Status:** Ready for planning

<domain>
## Phase Boundary

감정 2개 선택 → 합성 프리뷰 확인 → 일기 작성 → 저장 → 젤리서랍 목록 조회까지 완성.
사용자가 앱의 핵심 루프를 처음부터 끝까지 실행할 수 있는 상태.

Requirements: JELLY-01, JELLY-02, JELLY-03, JELLY-04, JELLY-05, DRAW-01, DRAW-02, DRAW-03, DRAW-04, QUAL-01, QUAL-02

</domain>

<decisions>
## Implementation Decisions

### 백엔드 API 준비 상황

- **D-01:** `JellyDrawerResDTO` 감정 필드(emo1Name, emo1Icon, emo2Name, emo2Icon) 백엔드 이미 준비됨 — 별도 협의 불필요, 바로 DTO 추가 후 사용.
- **D-02:** 감정 아이콘 형식: 이미지 URL (String) → Glide로 ImageView에 로딩. 로컬 drawable 리소스 ID 아님.
- **D-03:** startAging API: `PATCH /api/jelly/{id}`, body에 status 필드 업데이트. 응답 형태 상관없이 isSuccessful() 체크 후 에이징룸으로 이동.

### 합성 젤리 프리뷰 (JELLY-02)

- **D-04:** 프리뷰 트리거: 감정 2개 모두 선택된 순간 즉시 API 호출 (debounce/버튼 없음).
- **D-05:** 프리뷰 엔드포인트: `GET /jellyComb/jelly-icon/{firstEmo}/{secondEmo}/false` — `isAwaken` 파라미터는 false 고정 (미숙성 상태).
- **D-06:** 응답 타입: `String` (이미지 URL) → Glide로 프리뷰 ImageView에 직접 로딩.
- **D-07:** 프리뷰 표시 내용: 이미지만 (AgedEmo 이름 없음). 이름 표시는 v2 검토.

### Claude's Discretion

- **로딩 인디케이터 구현 (QUAL-01):** `Resource<T>`는 success/error만 있음. loading 상태 추가 방식(Resource에 LOADING 추가 vs 별도 `MutableLiveData<Boolean> isLoading`) 중 더 적합한 방식 선택.
- **에러 메시지 한국어화 (QUAL-02):** 공통 유틸 또는 각 Activity 내 switch 처리 중 코드 중복을 최소화하는 방식 선택.
- **회전 버그 (LiveData 캐싱):** `if (liveData != null) return` 패턴으로 ViewModel에서 중복 호출 방지. Researcher가 기존 ViewModel 패턴 확인 후 일관성 있게 적용.
- **감정 선택 UI:** BasicEmoAdapter + RecyclerView 기존 구조 유지. 2개 선택 제한 로직은 Adapter 또는 ViewModel 중 적합한 위치 결정.

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 요구사항 및 로드맵
- `.planning/REQUIREMENTS.md` — JELLY-01~05, DRAW-01~04, QUAL-01, QUAL-02 상세 요구사항
- `.planning/ROADMAP.md` — Phase 2 Key Tasks, Backend Coordination 항목
- `.planning/PROJECT.md` — 기술 스택 제약 (Android Native Java, Retrofit2, MVVM, No DI)

### 핵심 API 서비스 파일
- `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombService.java` — `getJellyIcon(Long, Long, Boolean)` → `Call<String>` 이미 정의됨
- `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyService.java` — jelly 저장/조회 API (startAging PATCH 포함 여부 확인 필요)
- `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/retrofit/BasicEmoService.java` — 감정 목록 GET

### 수정 대상 주요 파일
- `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java` — "숙성시키기" onClick, 에러 UI 완성
- `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/TodayJellyActivity.java` — EditText, 저장 버튼, 성공/에러 처리
- `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyDrawerResDTO.java` — 감정 필드 추가
- `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/BasicEmoAdapter.java` — ListAdapter 마이그레이션, 2개 선택 제한

### Phase 1 확립된 패턴 (필독)
- `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java` — `createService(Class, Context)` 시그니처
- `app/src/main/java/com/mindJellyProject/mindjelly/common/SessionManager.java` — `getUserId()` 반환 타입 `long`, sentinel `-1L`
- `app/src/main/java/com/mindJellyProject/mindjelly/common/Resource.java` — success/error 래퍼 (loading 없음)

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `JellyCombService.getJellyIcon()` — 합성 프리뷰 엔드포인트 이미 선언됨, Repository에서 호출만 구현하면 됨.
- `BasicEmoRepository` + `BasicEmoViewModel` — 감정 목록 조회 이미 구현됨. Phase 1에서 AndroidViewModel로 전환 완료.
- `JellyCombRepository` + `JellyCombViewModel` — Phase 1에서 AndroidViewModel로 전환 완료. 프리뷰 메서드 추가만 필요.
- Glide — 이미 의존성에 포함됨 (기존 코드에서 이미지 로딩에 사용 중).

### Established Patterns
- **AndroidViewModel + Repository:** Phase 1에서 전 도메인 적용. Phase 2 신규 ViewModel도 동일 패턴.
- **Repository 콜백:** `enqueue()` + `MutableLiveData.postValue()` — 기존 패턴 그대로.
- **인증된 API 호출:** `RetrofitClient.createService(Service.class, context)` — 모든 Repository 생성자에서 사용.

### Integration Points
- `TodayJellyActivity` → `BasicEmoViewModel` (감정 목록 로딩) + `JellyCombViewModel` (프리뷰 호출) + `JellyViewModel` (저장)
- `JellyDrawerActivity` → `JellyViewModel` (목록 조회) + startAging 호출 후 `AgingRoomActivity`로 이동
- 감정 2개 선택 감지 → `JellyCombViewModel.getPreview(firstEmoId, secondEmoId)` 즉시 호출

</code_context>

<specifics>
## Specific Ideas

- 합성 프리뷰 API 파라미터: `isAwaken = false` (Boolean, 하드코딩) — 서랍 저장 전이므로 미숙성 상태
- 프리뷰 ImageView: 감정 2개가 선택되기 전에는 placeholder 표시 (Glide placeholder 옵션 활용)
- startAging 성공 → `AgingRoomActivity`로 이동, 실패 → 한국어 Toast 에러 메시지

</specifics>

<deferred>
## Deferred Ideas

- **합성 AgedEmo 이름 표시:** 프리뷰에 이름 추가는 v2 검토 — MVP는 이미지만 (D-07)
- **일기 글자 수 표시:** REQUIREMENTS에 없음, v2 검토
- **숙성 후 성찰 일기:** Out of Scope (REQUIREMENTS.md)
- **AgingRoom 카운트다운 / JellyMuseum 컬렉션:** Phase 3, 4 범위

</deferred>

---

*Phase: 2-핵심루프*
*Context gathered: 2026-05-14*
