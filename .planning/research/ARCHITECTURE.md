# Architecture Research — MindJelly Android
<!-- researched: 2026-05-12 | confidence: HIGH -->

## Key Findings

### 1. 서버 측 상태 권위 (Server-Side State Authority) — 필수
**숙성 전환(WAITING → AGING → MATURED)은 반드시 Spring Boot 백엔드가 소유해야 한다.**
클라이언트 측 타이머(WorkManager, AlarmManager)는 앱 재시작, 삭제, 시스템 시간 변경에 취약하다.
WorkManager는 v2 푸시 알림 기능에만 고려할 것.
클라이언트는 매 화면 재진입 시 서버에서 상태를 읽기만 한다.

### 2. SessionManager 컴포넌트 누락 — Auth의 블로커
JWT 토큰 저장소와 OkHttp 인터셉터가 없다. 이것이 먼저 구현되지 않으면 인증된 모든 엔드포인트가 작동하지 않는다. Phase 1에서 반드시 구현.

### 3. JellyDrawerResDTO 백엔드 계약 추가 필요
현재 DTO: `jellyId`, `jellyCombId`, `isAging`, `createDate`
클라이언트가 MATURED 상태를 구분하려면 nullable `agedEmoId (Long)` 필드가 필요하다. 백엔드와 협의 필요.

### 4. 감정 조합 해결은 서버에서
`JellyCombination` 매핑 테이블은 서버 관리. 기존 `JellyCombRepository/Service` 스캐폴드 구조는 올바름.
선택 중 API 레이턴시가 느리면 `JellyCombViewModel`의 인메모리 세션 캐시 허용.

### 5. LiveData 회전(Rotation) 버그
모든 Repository 메서드가 매 호출마다 새 `MutableLiveData` 인스턴스를 생성한다.
ViewModel이 LiveData를 필드에 캐시하고 동일 인스턴스를 반환해야 한다. Hilt 불필요.

## 컴포넌트 경계

| 컴포넌트 | 책임 | 통신 대상 |
|---------|------|---------|
| `TodayJellyActivity` | 감정 2개 선택 + 일기 텍스트 + Jelly POST | `BasicEmoViewModel`, `JellyViewModel` |
| `JellyDrawerActivity` | WAITING/AGING/MATURED 뱃지 목록; startAging 트리거 | `JellyViewModel` |
| `AgingRoomActivity` | 숙성 중 목록 + 카운트다운; onResume에서 완료 감지 | `AgedEmoViewModel` |
| `jellyMuseumActivity` | 숙성 완료 컬렉션 + 성찰 일기 | `AgedEmoViewModel` |
| `SessionManager` (신규) | JWT 저장소 + OkHttp 인터셉터 | `RetrofitClient` |
| `JellyCombViewModel` | (emo1, emo2) → jellyCombId 해결; 선택적 캐시 | `JellyCombRepository` |

## 데이터 플로우

```
TodayJelly: BasicEmo GET → 그리드 표시 → 2개 선택
         → JellyComb GET (페어 해결) → jellyCombId
         → Jelly POST (jellyCombId + 일기 내용 포함)
         → JellyDrawer로 이동

JellyDrawer: Jelly 목록 GET (WAITING/AGING/MATURED 상태 포함)
           → 사용자가 WAITING 항목의 "숙성시키기" 탭
           → Jelly PUT startAging
           → AgingRoom으로 이동

AgingRoom: onResume → AgedEmo 숙성중 목록 GET (카운트다운 표시)
         → 목록이 비어있을 때 (비어있지 않았다가): Museum 이동 유도

Museum: onResume → AgedEmo 뮤지엄 목록 GET (숙성 완료 컬렉션)
```

**클라이언트는 상태를 변경하지 않는다. 매 화면 재진입 시 서버에서 상태를 읽는다.**

## 권장 빌드 순서

```
Phase 1 — Auth 기반
  SessionManager + OkHttp 인터셉터 → 로그인 → 회원가입 → SplashActivity 라우팅

Phase 2 — 핵심 쓰기 경로
  BasicEmo 목록 → JellyComb 해결 → Jelly 생성 → JellyDrawer 목록

Phase 3 — 숙성 메커니즘
  startAging PUT → AgingRoom 카운트다운 → 숙성 완료 감지

Phase 4 — 뮤지엄
  AgedEmo 뮤지엄 목록 → 합성 감정 표시 → 숙성 후 성찰 일기

Phase 5 — 품질 강화
  LiveData 회전 수정 → 에러 UI → 환경별 BASE_URL → HTTPS
```

**Phase 3/4 개발 블로커:** 7일 대기는 개발 중 테스트 불가.
백엔드에서 **`PUT /api/jelly/{id}/forceMatured` 테스트 엔드포인트** 제공 필요 (dev 환경 전용, 프로덕션 비노출).

## 안티패턴 (피해야 할 것)

| 안티패턴 | 올바른 방법 |
|---------|----------|
| 클라이언트 측 숙성 타이머 (WorkManager) | 서버 크론으로 상태 전환, 클라이언트는 폴링만 |
| Java 정적 HashMap으로 감정 조합 캐시 | 서버 API 호출; 서버 테이블 업데이트 시 클라이언트 자동 반영 |
| Activity 간 Jelly 객체 Serializable 전달 | `jellyId`만 전달, 대상 Activity에서 다시 fetch |
| Repository 메서드 호출마다 새 LiveData 생성 | ViewModel 필드에 LiveData 캐시, 동일 인스턴스 반환 |
| 백그라운드 스레드에서 `setValue()` | 항상 `postValue()` 사용 (기존 코드 패턴 유지) |

## 오픈 질문 / 백엔드 협의 필요

| 질문 | 영향 |
|------|------|
| `startAging` API 계약: `PUT /api/jelly/{id}/startAging` vs `PATCH`? 응답 형태? | AgingRoom 화면 구현 |
| 숙성 감지 API: 전용 `GET /api/agedEmo/aging?userId=` 엔드포인트 존재 여부 | AgingRoom onResume 폴링 구현 |
| `JellyCombination.isAwaken` 필드의 비즈니스 의미 | Museum 표시 로직 |
| `JellyDrawerResDTO`에 `agedEmoId` 추가 여부 | 젤리서랍 숙성 상태 표시 |
| access token + refresh token 쌍 발급 여부 vs 단일 JWT | `AuthInterceptor` 401 처리 방식 |
