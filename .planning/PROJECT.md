# MindJelly (마음젤리)

## What This Is

MindJelly는 매일 자신의 감정을 2가지 젤리로 선택하고 일기를 기록하는 Android 감정 일기 앱이다. 기록된 젤리는 일주일 동안 '숙성'되고, 숙성 완료 후 두 감정이 합성된 새로운 감정이 젤리뮤지엄에 공개된다. 개인 전용 감정 일기로, 지금의 나와 일주일 후의 나를 연결하는 감정 성장 기록 도구다.

## Core Value

오늘 선택한 두 감정이 일주일 후 합성된 새로운 감정으로 숙성되어 나만의 감정 성장 기록이 쌓인다.

## Requirements

### Validated

<!-- 기존 코드베이스에서 이미 구축된 것들 -->

- ✓ MVVM + Repository + Retrofit2 아키텍처 — 전 도메인에 일관되게 적용됨
- ✓ Resource<T> API 응답 래퍼 (success/error) — `common/Resource.java`
- ✓ RetrofitClient 싱글톤 — `common/RetrofitClient.java`
- ✓ 도메인별 패키지 구조 (users, basicEmo, jellyDomain, agedEmoDomain, common)
- ✓ SplashActivity 진입점 및 MainActivity 허브 화면 구조
- ✓ 전체 화면 선언 (AndroidManifest.xml에 모든 Activity 등록됨)

<!-- Validated in Phase 1: Auth 기반 (2026-05-14) -->

- ✓ 이메일 + 비밀번호로 회원가입 (이메일 인증 안내 포함) — `SignUpActivity`, `UserRepository` (AUTH-01)
- ✓ 이메일 + 비밀번호로 로그인 및 세션 유지 (앱 재시작 후에도 유지) — `LoginActivity`, `SessionManager`, `SplashActivity` (AUTH-02)
- ✓ 모든 API 요청에 JWT Bearer 토큰 자동 포함 + 401 자동 로그아웃 — `AuthInterceptor`, `RetrofitClient` (AUTH-03)
- ✓ userId 하드코딩 제거, 모든 Repository가 Context 기반 Retrofit 사용 — `JellyDrawerActivity`, 6개 Repository (QUAL-05)

### Active

<!-- v1 목표 — 출시하고 검증할 기능들 -->

**인증 (Auth)**
- [x] 이메일 + 비밀번호로 회원가입 (이메일 인증 포함) — Phase 1 완료
- [x] 이메일 + 비밀번호로 로그인 및 세션 유지 — Phase 1 완료
- [ ] 로그아웃
- [ ] 이메일 찾기 / 비밀번호 재설정

**오늘의 마음젤리 (Today's Jelly)**
- [ ] 미리 정의된 감정 목록에서 젤리 2가지 선택
- [ ] 오늘의 일기 자유 작성 (글자 수 제한 없음)
- [ ] 작성 완료 후 젤리서랍에 저장

**젤리서랍 (JellyDrawer)**
- [ ] 날짜별 과거 일기 + 젤리 2개 목록 조회
- [ ] 각 항목에 숙성 상태 표시 (대기중 / 숙성중 / 숙성완료)
- [ ] '숙성시키기' 버튼 클릭 → 에이징룸으로 이동

**에이징룸 (AgingRoom)**
- [ ] 현재 숙성 중인 젤리들 목록 표시 (7일 카운트다운)
- [ ] 7일 경과 후 자동으로 젤리뮤지엄으로 이동

**젤리뮤지엄 (JellyMuseum)**
- [ ] 숙성 완료된 젤리들 컬렉션 조회
- [ ] 두 감정의 합성 결과 새로운 감정 표시
- [ ] 숙성 후 새로운 일기 작성 가능

**감정 합성 (Combination Logic)**
- [ ] 2가지 basicEmo 조합 → 합성 AgedEmo 매핑 테이블 (서버/앱 정의)

### Out of Scope

- 소셜/공유 기능 — 완전 개인 일기, 다른 사람과 공유 없음
- 소셜 로그인 (Google, Kakao 등) — 이메일 인증 방식 우선, 추후 검토
- 푸시 알림 (숙성 완료 알림) — v1 범위 밖, 추후 검토
- 젤리 커스터마이즈 — 미리 정해진 감정 목록 사용, 사용자 생성 없음
- 젤리 선택 취소/삭제 — 기록 무결성 유지

## Context

- **기존 코드베이스:** 2025년 1월 기준 API 연결 작업이 진행된 상태. 모든 화면 Activity가 선언되어 있으나 일부 onClick 핸들러가 비어있음 (MainActivity의 AgingRoom, JellyMuseum, SelectionBox 버튼).
- **백엔드:** Spring Boot REST API. 개발 환경 BASE_URL은 `http://10.0.2.2:8080/` (Android 에뮬레이터 로컬호스트). 실기기 테스트를 위해 환경별 URL 설정이 필요함.
- **인증 완료 (Phase 1):** SessionManager(EncryptedSharedPreferences), AuthInterceptor(JWT Bearer 자동 주입 + 401 자동 로그아웃), RetrofitClient(Context 기반) 구현 완료. HTTP 통신은 에뮬레이터 개발용 debug 오버레이로 허용, release는 HTTPS 전용.
- **감정 목록:** basicEmo 도메인에서 API로 감정 목록을 받아옴. 합성 결과(AgedEmo)도 서버 정의.
- **팀 규모:** 1인 개발 (Jinhyeok)

## Constraints

- **Tech Stack**: Android Native Java, Retrofit2, LiveData, MVVM — 기존 아키텍처 유지
- **Backend**: Spring Boot REST API (별도 서버 프로젝트) — 클라이언트만 이 레포
- **Min SDK**: AndroidManifest 기준 (libs.versions.toml 확인 필요)
- **No DI Framework**: 현재 ViewModel이 Repository를 직접 생성. Hilt 도입은 v1 후 검토.

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| 이메일 인증 로그인만 v1 지원 | 빠른 구현, 소셜 로그인은 복잡도 높음 | — Pending |
| 미리 정의된 감정 목록 (서버 제공) | 사용자 커스텀보다 감정 합성 로직 단순화 | — Pending |
| 두 감정 합성 결과를 서버 테이블로 관리 | AI 없이 예측 가능한 결과, 추후 확장 가능 | — Pending |
| 숙성 7일 고정 | 일주일이라는 자연스러운 감정 성장 주기 | — Pending |
| 완전 개인 일기 (소셜 없음) | v1 핵심 가치에 집중, 소셜은 복잡도 증가 | — Pending |
| MVVM + Retrofit2 유지 | 기존 코드베이스와 일관성, 학습 비용 없음 | ✓ Good |

## Evolution

This document evolves at phase transitions and milestone boundaries.

**After each phase transition** (via `/gsd-transition`):
1. Requirements invalidated? → Move to Out of Scope with reason
2. Requirements validated? → Move to Validated with phase reference
3. New requirements emerged? → Add to Active
4. Decisions to log? → Add to Key Decisions
5. "What This Is" still accurate? → Update if drifted

**After each milestone** (via `/gsd-complete-milestone`):
1. Full review of all sections
2. Core Value check — still the right priority?
3. Audit Out of Scope — reasons still valid?
4. Update Context with current state

---
*Last updated: 2026-05-14 after Phase 1 completion*
