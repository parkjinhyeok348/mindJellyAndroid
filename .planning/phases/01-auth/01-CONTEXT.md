# Phase 1: Auth 기반 - Context

**Gathered:** 2026-05-12
**Status:** Ready for planning

<domain>
## Phase Boundary

실제 사용자로 로그인하고, 앱 재시작 후에도 세션이 유지되며, 모든 API 요청에 JWT Bearer 토큰이 자동으로 포함되는 앱 완성.
userId 하드코딩(1L) 제거 포함.

Requirements: AUTH-01, AUTH-02, AUTH-03, QUAL-05

</domain>

<decisions>
## Implementation Decisions

### 이메일 인증 UX (AUTH-02)
- **D-01:** 회원가입 API 성공 시 별도 인증 대기 화면 없음 — Toast 또는 다이얼로그로 "발송된 이메일을 확인하세요" 안내 후 LoginActivity로 이동.
- **D-02:** 이메일 인증 완료 전 로그인 시도 시 서버 에러 응답에서 인증 미완료를 구별하여 "이메일 인증이 필요합니다" 한국어 메시지 표시. 일반 로그인 실패("이메일 혹은 비밀번호가 틀렸습니다")와 명확히 구분.
- **D-03:** 인증 메일 재발송 기능 없음 — v1에서는 안내 메시지만. 재발송은 v2 검토.

### Claude's Discretion
- **Splash 라우팅:** 앱 재시작 시 토큰 존재 여부만 체크 → 토큰 있으면 MainActivity, 없으면 LoginActivity. 만료 토큰은 첫 인증된 API 실패 시 LoginActivity로 이동(401 처리). 별도 서버 유효성 검증 API 콜 불필요.
- **JWT userId 추출 (QUAL-05):** JWT payload를 Base64 디코딩하여 userId 필드 파싱. 추가 라이브러리 없이 android.util.Base64 사용.
- **회원가입 필드:** UserSaveReqDTO 기준으로 필드 결정. Researcher가 기존 SignUpActivity + DTO 확인 후 구현 범위 판단.

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 요구사항 및 로드맵
- `.planning/REQUIREMENTS.md` — AUTH-01, AUTH-02, AUTH-03, QUAL-05 상세 요구사항
- `.planning/ROADMAP.md` — Phase 1 Key Tasks, Pitfall Watch (SessionManager 없으면 이후 Phase 테스트 불가)
- `.planning/PROJECT.md` — 기술 스택 제약 (Android Native Java, Retrofit2, MVVM, No DI), 보안 미비 사항

### 수정 대상 파일
- `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java` — OkHttpClient + AuthInterceptor 추가 대상
- `app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java` — JWT 저장 로직 추가 대상
- `app/src/main/java/com/mindJellyProject/mindjelly/users/retrofit/UserRepository.java` — 기존 login() 반환 타입 확인 (LiveData<Resource<String>>, String = JWT)
- `app/src/main/AndroidManifest.xml` — network_security_config.xml 연결 대상

### 기존 공통 인프라
- `app/src/main/java/com/mindJellyProject/mindjelly/common/Resource.java` — success/error 래퍼 (loading 상태 없음 — Phase 2에서 추가 예정)

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `Resource<T>` — 모든 Repository에서 이미 사용 중. SessionManager도 동일 패턴으로 LiveData 래핑 가능.
- `RetrofitClient.createService()` — AuthInterceptor 추가 시 기존 호출부 변경 없이 확장 가능 (싱글톤 재초기화 필요).
- `LoginActivity` — 이메일/비밀번호 UI + ViewModel 연결 완성. 토큰 저장 + 에러 메시지 세분화만 추가하면 됨.

### Established Patterns
- **MVVM + Repository:** 신규 SessionManager도 동일 레이어 구조 준수. AuthInterceptor는 RetrofitClient 레이어에 위치.
- **Repository 콜백 패턴:** enqueue() + MutableLiveData.postValue() — UserRepository.login()이 이미 구현됨, 패턴 그대로 재사용.
- **No DI:** ViewModel이 Repository를 직접 생성. SessionManager는 싱글톤 또는 Application Context 주입 방식 사용.

### Integration Points
- `RetrofitClient` → OkHttpClient + AuthInterceptor 추가 시, AuthInterceptor는 SessionManager에서 토큰 읽음.
- `SplashActivity` → SessionManager.hasToken() 체크 후 Login vs Main 분기.
- `JellyDrawerActivity` → 하드코딩된 userId = 1L 제거, SessionManager.getUserId() 사용.
- 401 응답 처리 → AuthInterceptor 또는 전역 에러 핸들러에서 SessionManager.clear() 후 LoginActivity로 이동.

</code_context>

<specifics>
## Specific Ideas

- 이메일 인증 미완료 에러 메시지: **"이메일 인증이 필요합니다"** (서버 에러 코드/메시지로 구별)
- 회원가입 후 이메일 안내: Toast 또는 AlertDialog (구현 시 판단)
- v1에서 재발송 버튼 없음 — LoginActivity에 추가 UI 불필요

</specifics>

<deferred>
## Deferred Ideas

- **로그아웃 (AUTH-04):** v2 이월 — REQUIREMENTS.md에 v2로 분류됨
- **비밀번호 재설정 (AUTH-05):** v2 이월
- **HTTP → HTTPS 전환 (QUAL-03):** v2 이월 — debug 빌드만 cleartext 허용하는 network_security_config는 Phase 1에서 구현, 프로덕션 HTTPS는 v2
- **에뮬레이터/실기기 BASE_URL 자동 전환 (QUAL-04):** v2 이월
- **인증 메일 재발송:** v2 검토 — 서버 API 존재 여부 미확인

</deferred>

---

*Phase: 1-Auth 기반*
*Context gathered: 2026-05-12*
