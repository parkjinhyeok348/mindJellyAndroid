# Pitfalls Research — MindJelly Android
<!-- researched: 2026-05-12 | confidence: HIGH (derived from codebase analysis) -->

## Critical Pitfalls

### 1. Auth 미구현이 모든 것을 막는다
**경고 신호:** 모든 API 호출이 401을 반환하거나 userId 하드코딩으로 우회 중
**예방 전략:** Phase 1에서 SessionManager + AuthInterceptor를 가장 먼저 구현. 이것 없이는 JellyDrawer, AgingRoom, Museum 어느 것도 실제 사용자 데이터로 테스트 불가.
**담당 Phase:** Phase 1

---

### 2. userId 하드코딩 (`1L`) — 데이터 오염
**경고 신호:** `JellyDrawerActivity.java`에서 `userId = 1L` 하드코딩 확인됨
**예방 전략:** Phase 1 완료 후 즉시 SessionManager에서 JWT 파싱하여 userId 추출. 하드코딩을 절대 커밋하지 말 것.
**담당 Phase:** Phase 1 완료 시점

---

### 3. LiveData 회전(Configuration Change) 버그
**경고 신호:** 화면 회전 시 API가 두 번 호출됨; Repository 메서드마다 `new MutableLiveData<>()` 생성
**예방 전략:** ViewModel이 LiveData를 필드로 보유하고 동일 인스턴스를 반환해야 함.
**담당 Phase:** Phase 2 (각 ViewModel 신규 작성 시 적용)

---

### 4. 클라이언트 측 7일 타이머 유혹
**경고 신호:** "WorkManager로 7일 카운트다운 구현하자"는 아이디어
**예방 전략:** 숙성 상태 전환은 서버 크론이 담당. 클라이언트는 화면 진입마다 서버 상태를 읽기만 함.
**담당 Phase:** Phase 3 설계 시

---

### 5. 개발 중 7일 대기 — 테스트 불가
**경고 신호:** Phase 3/4 기능을 개발하는데 실제 7일을 기다려야 테스트 가능
**예방 전략:** 백엔드에 `PUT /api/jelly/{id}/forceMatured` 테스트 엔드포인트 요청 (dev 환경 전용). 이 엔드포인트 없이 AgingRoom → Museum 전환 플로우 개발 불가.
**담당 Phase:** Phase 3 시작 전 백엔드 협의

---

### 6. DTO 데이터 갭 — 화면 구현 블로커
**경고 신호:** JellyDrawerActivity에 감정 아이콘/이름이 표시되지 않음
**예방 전략:** `JellyDrawerResDTO`에 emo1Name, emo1Icon, emo2Name, emo2Icon 필드 추가. 백엔드 API 계약 변경 필요.
**담당 Phase:** Phase 2

---

### 7. HTTP cleartext — 보안 취약점
**경고 신호:** `AndroidManifest.xml`의 `usesCleartextTraffic="true"` 설정
**예방 전략:** `network_security_config.xml`로 cleartext를 debug 빌드에만 허용. 프로덕션은 HTTPS만.
**담당 Phase:** Phase 1

---

### 8. 빈 상태(Empty State) 누락 — 신규 사용자 혼란
**경고 신호:** JellyDrawer, AgingRoom, Museum이 데이터 없을 때 빈 화면만 표시
**예방 전략:** 각 목록 화면에 empty state 안내 텍스트 추가.
**담당 Phase:** 각 화면 구현 Phase에서 함께 처리
