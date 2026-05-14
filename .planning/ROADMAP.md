# Roadmap: MindJelly (마음젤리)

**Generated:** 2026-05-12
**Mode:** Vertical MVP
**Total Phases:** 4 | **v1 Requirements:** 21 | **Coverage:** 100% ✓

---

## Phase Overview

| # | Phase | Goal | Requirements | Success Criteria |
|---|-------|------|--------------|-----------------|
| 1 | Auth 기반 ✓ | 실제 사용자로 로그인하고 인증된 API 호출 가능 | AUTH-01, AUTH-02, AUTH-03, QUAL-05 | 4 — Complete 2026-05-14 |
| 2 | 핵심 루프 | 감정 2개 선택 → 일기 작성 → 저장 → 서랍 조회 완성 | JELLY-01~05, DRAW-01~04, QUAL-01, QUAL-02 | 5 |
| 3 | 숙성 메커니즘 | 젤리를 숙성 시작하고 에이징룸에서 카운트다운 확인 | AGING-01, AGING-02, AGING-03 | 3 |
| 4 | 젤리뮤지엄 | 숙성 완료 젤리 컬렉션 조회 및 합성 감정 확인 | MUSE-01, MUSE-02, MUSE-03 | 3 |

---

## Phase Details

### Phase 1: Auth 기반
**Goal:** 실제 사용자로 로그인하고 인증된 API 호출이 가능한 앱 완성
**Mode:** mvp
**Requirements:** AUTH-01, AUTH-02, AUTH-03, QUAL-05
**Success Criteria:**
1. 신규 사용자가 이메일 + 비밀번호로 회원가입하고 이메일 인증을 받을 수 있다
2. 가입된 사용자가 로그인 후 앱을 재시작해도 세션이 유지된다
3. 모든 API 요청에 JWT Bearer 토큰이 자동으로 포함된다 (AuthInterceptor 동작 확인)
4. JellyDrawerActivity에서 userId 하드코딩(1L)이 제거되고 JWT에서 추출된 userId가 사용된다

**Key Tasks:**
- `SessionManager.java` 신규 생성 — EncryptedSharedPreferences JWT 저장소
- `AuthInterceptor.java` 신규 생성 — OkHttp Bearer 토큰 주입
- `RetrofitClient.java` 리팩터 — OkHttpClient + AuthInterceptor 적용
- `LoginActivity.java` 완성 — 로그인 성공 시 JWT 저장 + MainActivity 이동
- `RegisterActivity.java` 완성 — 회원가입 플로우
- `SplashActivity.java` 업데이트 — 세션 유무에 따라 Login vs Main 라우팅
- `network_security_config.xml` 추가 — cleartext를 debug 빌드에만 허용

**Pitfall Watch:**
- SessionManager 없이는 이후 모든 Phase 테스트 불가 → 이것부터
- userId 하드코딩 Phase 1 완료 시 반드시 제거

**Plans:** 5 plans

Plans:
- [x] 01-A-PLAN.md — SessionManager + gradle security-crypto (Wave 1)
- [x] 01-B-PLAN.md — AuthInterceptor + RetrofitClient OkHttpClient (Wave 1)
- [x] 01-C-PLAN.md — LoginActivity JWT 저장 + 에러 세분화 + SplashActivity 라우팅 + network_security_config (Wave 2)
- [x] 01-D-PLAN.md — SignUpActivity createUser 결과 옵저빙 완성 (Wave 2)
- [x] 01-E-PLAN.md — userId 하드코딩 제거 + 6개 Repository Context 업데이트 + 401 처리 (Wave 3)


---

### Phase 2: 핵심 루프
**Goal:** 감정 2개 선택 → 일기 작성 → 저장 → 젤리서랍 목록 조회까지 완성
**Mode:** mvp
**Requirements:** JELLY-01, JELLY-02, JELLY-03, JELLY-04, JELLY-05, DRAW-01, DRAW-02, DRAW-03, DRAW-04, QUAL-01, QUAL-02
**Success Criteria:**
1. 사용자가 감정 이름이 표시된 그리드에서 젤리 2개를 선택하면 합성 프리뷰가 실시간으로 업데이트된다
2. 일기 텍스트를 작성하고 저장 버튼을 누르면 성공 피드백 후 젤리서랍으로 이동한다
3. 젤리서랍에서 날짜·감정 아이콘 2개·숙성 상태 배지가 포함된 기록 목록을 볼 수 있다
4. 모든 네트워크 요청에 로딩 인디케이터가 표시되고 에러 시 한국어 메시지가 나온다
5. 기록이 없을 때 젤리서랍에 빈 상태 안내가 표시된다

**Key Tasks:**
- `item_basic_emo.xml` 업데이트 — 감정 이름 TextView 추가
- `BasicEmoAdapter.java` 업데이트 — ListAdapter + DiffUtil 마이그레이션
- `TodayJellyActivity.java` 완성 — EditText 추가, 저장 버튼, 성공/에러 처리
- `JellyDrawerResDTO.java` 업데이트 — emo1Name, emo1Icon, emo2Name, emo2Icon 추가 (백엔드 협의 후)
- `JellyDrawerAdapter.java` 업데이트 — ListAdapter + 날짜·아이콘·상태 배지 표시
- `JellyDrawerActivity.java` 완성 — "숙성시키기" onClick 구현, 에러 UI
- 모든 ViewModel LiveData 캐싱 패턴 적용 (회전 버그 수정)
- 공통 로딩 인디케이터 유틸리티

**Backend Coordination:**
- JellyDrawerResDTO에 감정 필드 추가 요청
- startAging API 계약 확인 (PUT vs PATCH, 응답 형태)

---

### Phase 3: 숙성 메커니즘
**Goal:** 젤리를 숙성 시작하고 에이징룸에서 카운트다운을 확인할 수 있다
**Mode:** mvp
**Requirements:** AGING-01, AGING-02, AGING-03
**Success Criteria:**
1. 젤리서랍에서 "숙성시키기" 후 에이징룸에서 해당 젤리의 D-day 카운트다운이 표시된다
2. 서버가 숙성 완료로 상태를 전환하면 에이징룸에서 뮤지엄 이동 안내가 나타난다
3. 숙성 중인 젤리가 없을 때 빈 상태 안내가 표시된다

**Key Tasks:**
- `AgingRoomActivity.java` 완성 — onResume 폴링, 카운트다운 리스트, 빈 상태
- AgedEmo API 연동 — 숙성중 목록 GET
- 상태 전환 감지 로직 (목록이 비어있지 않다가 비어있을 때 → 뮤지엄 이동 유도)

**Pitfall Watch:**
- 7일 대기 테스트를 위한 백엔드 `PUT /api/jelly/{id}/forceMatured` 엔드포인트 사전 요청 필수
- 클라이언트 측 타이머(WorkManager) 구현 금지 — 서버 상태를 읽기만 할 것

---

### Phase 4: 젤리뮤지엄
**Goal:** 숙성 완료된 젤리 컬렉션을 그리드로 보고 합성 감정을 확인할 수 있다
**Mode:** mvp
**Requirements:** MUSE-01, MUSE-02, MUSE-03
**Success Criteria:**
1. 숙성 완료된 젤리들이 그리드 레이아웃으로 표시된다
2. 각 항목에서 두 감정의 합성 결과인 새로운 감정(AgedEmo) 이름·이미지가 표시된다
3. 숙성 완료 젤리가 없을 때 빈 상태 안내가 표시된다

**Key Tasks:**
- `jellyMuseumActivity.java` 완성 — AgedEmo 컬렉션 그리드 RecyclerView
- AgedEmoImage API 연동 — 합성 감정 이미지 로딩
- 숙성 완료 reveal 애니메이션 (scale-in)
- 뮤지엄 그리드 레이아웃 XML

---

## Requirement Traceability

| Requirement | Phase | Status |
|-------------|-------|--------|
| AUTH-01 | Phase 1 | Complete |
| AUTH-02 | Phase 1 | Complete |
| AUTH-03 | Phase 1 | Complete |
| QUAL-05 | Phase 1 | Complete |
| JELLY-01 | Phase 2 | Pending |
| JELLY-02 | Phase 2 | Pending |
| JELLY-03 | Phase 2 | Pending |
| JELLY-04 | Phase 2 | Pending |
| JELLY-05 | Phase 2 | Pending |
| DRAW-01 | Phase 2 | Pending |
| DRAW-02 | Phase 2 | Pending |
| DRAW-03 | Phase 2 | Pending |
| DRAW-04 | Phase 2 | Pending |
| QUAL-01 | Phase 2 | Pending |
| QUAL-02 | Phase 2 | Pending |
| AGING-01 | Phase 3 | Pending |
| AGING-02 | Phase 3 | Pending |
| AGING-03 | Phase 3 | Pending |
| MUSE-01 | Phase 4 | Pending |
| MUSE-02 | Phase 4 | Pending |
| MUSE-03 | Phase 4 | Pending |

**Coverage:** 21/21 v1 requirements mapped ✓

---
*Generated: 2026-05-12*
