# Research Summary — MindJelly Android
<!-- synthesized: 2026-05-12 | sources: STACK.md, FEATURES.md, ARCHITECTURE.md, PITFALLS.md -->

## Stack 권고

**기존 유지:** Java, Retrofit2, OkHttp, LiveData/ViewModel, RecyclerView
**Phase 1 추가:** `androidx.security:security-crypto:1.1.0` (JWT 저장), OkHttp AuthInterceptor (헤더 주입)
**패턴 업그레이드:** RecyclerView 어댑터 → ListAdapter + DiffUtil
**쓰지 말 것:** Room DB, Hilt, Kotlin Coroutines, Glide, Firebase (v1 범위 밖)

## Table Stakes (반드시 있어야 할 것)

1. Auth + JWT 헤더 주입 — 모든 API의 게이트
2. 감정 선택 그리드 (이름 표시 포함)
3. 일기 텍스트 입력 + 저장 버튼 + 성공 피드백
4. 날짜/감정 아이콘 포함한 JellyDrawer 목록
5. AgingRoom 카운트다운 표시
6. Museum 컬렉션 그리드
7. 로딩 인디케이터 + 에러 메시지 한국어화
8. 빈 상태(Empty State) 안내

## 차별화 요소 (MindJelly만의 것)

- 2감정 동시 선택 → 실시간 조합 프리뷰 (이미 BasicEmoAdapter에 max-2 제약 구현됨)
- 7일 숙성 메타포 + 카운트다운 UX (기대감 메커니즘)
- 숙성 완료 reveal 순간 (scale-in 애니메이션)
- 뮤지엄 그리드 컬렉션 (감정 성장 기록)

## 아키텍처 결정

- **서버 측 상태 권위:** WAITING → AGING → MATURED 전환은 Spring Boot 크론이 담당. 클라이언트는 화면 진입마다 서버 상태를 읽기만 함.
- **SessionManager 신규 컴포넌트:** JWT 저장소 + OkHttp 인터셉터. Phase 1 필수.
- **DTO 계약 추가 필요:** JellyDrawerResDTO에 emo1Name/emo1Icon/emo2Name/emo2Icon 추가. 백엔드 협의 필요.

## Watch Out For (최우선 주의사항)

1. **Auth 먼저** — SessionManager 없이는 모든 기능이 작동 안 함
2. **userId 하드코딩 제거** — JellyDrawerActivity `1L` 즉시 교체
3. **7일 테스트** — 백엔드 forceMatured 엔드포인트 없으면 Phase 3/4 개발 불가
4. **LiveData 인스턴스 캐싱** — 화면 회전마다 API 중복 호출 방지
5. **HTTP cleartext** — network_security_config.xml로 debug 전용 처리

## 권장 Phase 순서

```
Phase 1 — Auth 기반 (SessionManager, 로그인, 회원가입, 보안 설정)
Phase 2 — 핵심 루프 (감정 선택, 일기 작성, JellyDrawer 목록)
Phase 3 — 숙성 메커니즘 (startAging, AgingRoom 카운트다운)
Phase 4 — 뮤지엄 (숙성 완료 컬렉션, 합성 감정 표시)
Phase 5 — 품질 강화 (에러 UI 한국어화, 환경별 URL, Empty State 정리)
```
