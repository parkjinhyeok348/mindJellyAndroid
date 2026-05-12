# Requirements: MindJelly (마음젤리)

**Defined:** 2026-05-12
**Core Value:** 오늘 선택한 두 감정이 일주일 후 합성된 새로운 감정으로 숙성되어 나만의 감정 성장 기록이 쌓인다.

## v1 Requirements

### 인증 (Auth)

- [ ] **AUTH-01**: 사용자가 이메일 + 비밀번호로 회원가입할 수 있다
- [ ] **AUTH-02**: 이메일 인증 후 계정이 활성화된다
- [ ] **AUTH-03**: 사용자가 이메일 + 비밀번호로 로그인하고 앱 재시작 후에도 세션이 유지된다

### 오늘의 젤리 (TodayJelly)

- [ ] **JELLY-01**: 사용자가 감정 목록 그리드에서 젤리 2개를 선택할 수 있다 (감정 이름 + 아이콘 표시)
- [ ] **JELLY-02**: 감정 2개 선택 시 합성 젤리 프리뷰가 실시간으로 표시된다
- [ ] **JELLY-03**: 사용자가 자유 텍스트로 오늘의 일기를 작성할 수 있다
- [ ] **JELLY-04**: 저장 버튼 클릭 시 젤리가 저장되고 성공 피드백이 표시된다
- [ ] **JELLY-05**: 저장 완료 후 자동으로 젤리서랍 화면으로 이동한다

### 젤리서랍 (JellyDrawer)

- [ ] **DRAW-01**: 사용자가 날짜별 과거 기록 목록을 조회할 수 있다 (날짜 + 감정 아이콘 2개 포함)
- [ ] **DRAW-02**: 각 기록에 숙성 상태 배지가 표시된다 (대기중 / 숙성중 / 숙성완료)
- [ ] **DRAW-03**: 대기중 항목의 "숙성시키기" 버튼 클릭 시 startAging API가 호출되고 에이징룸으로 이동한다
- [ ] **DRAW-04**: 기록이 없을 때 빈 상태 안내 메시지가 표시된다

### 에이징룸 (AgingRoom)

- [ ] **AGING-01**: 현재 숙성 중인 젤리 목록과 D-day 카운트다운이 표시된다
- [ ] **AGING-02**: 숙성 완료 감지 시 젤리뮤지엄 이동 안내가 표시된다
- [ ] **AGING-03**: 숙성 중인 젤리가 없을 때 빈 상태 안내 메시지가 표시된다

### 젤리뮤지엄 (JellyMuseum)

- [ ] **MUSE-01**: 숙성 완료된 젤리 컬렉션을 그리드로 조회할 수 있다
- [ ] **MUSE-02**: 두 감정의 합성 결과인 새로운 감정(AgedEmo)이 표시된다
- [ ] **MUSE-03**: 숙성 완료 젤리가 없을 때 빈 상태 안내 메시지가 표시된다

### 공통 품질 (Quality)

- [ ] **QUAL-01**: 모든 네트워크 요청 중 로딩 인디케이터가 표시된다
- [ ] **QUAL-02**: API 에러 발생 시 한국어 사용자 친화적 메시지가 표시된다
- [ ] **QUAL-05**: 로그인 사용자의 userId가 JWT에서 추출되어 사용된다 (하드코딩 1L 제거)

## v2 Requirements

### 인증 (Auth)

- **AUTH-04**: 사용자가 로그아웃할 수 있다
- **AUTH-05**: 사용자가 이메일 링크로 비밀번호를 재설정할 수 있다

### 보안 / 환경 설정

- **QUAL-03**: HTTP cleartext가 debug 빌드에만 허용되고 프로덕션은 HTTPS만 사용한다
- **QUAL-04**: 에뮬레이터와 실기기에서 다른 BASE_URL이 자동으로 사용된다

## Out of Scope

| Feature | Reason |
|---------|--------|
| 소셜/공유 기능 | 완전 개인 일기, 다른 사람과 공유 없음 |
| 소셜 로그인 (Google, Kakao) | 이메일 인증 방식 우선, 추후 검토 |
| 푸시 알림 (숙성 완료 알림) | v1 범위 밖 |
| 젤리 커스터마이즈 | 미리 정해진 감정 목록, 사용자 생성 없음 |
| 과거 기록 수정/삭제 | 기록 무결성 유지 |
| 통계/차트 | 30+ 엔트리 이후로 미룸 |
| 다크모드 | 단일 테마 v1 |
| 숙성 후 성찰 일기 | v2 이월 |
| 연속 기록 스트릭 | 스트릭 불안이 차분한 일기 방해 |

## Traceability

| Requirement | Phase | Status |
|-------------|-------|--------|
| AUTH-01 | Phase 1 | Pending |
| AUTH-02 | Phase 1 | Pending |
| AUTH-03 | Phase 1 | Pending |
| QUAL-05 | Phase 1 | Pending |
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

**Coverage:**
- v1 requirements: 21 total
- Mapped to phases: 21
- Unmapped: 0 ✓

---
*Requirements defined: 2026-05-12*
*Last updated: 2026-05-12 after initial definition*
