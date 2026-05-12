---
phase: 01-auth
plan: E
type: execute
wave: 4
depends_on:
  - 01-A
  - 01-B
  - 01-C
files_modified:
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java
  - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmo/retrofit/AgedEmoRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmoImage/retrofit/AgedEmoImageRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/retrofit/BasicEmoRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyImage/retrofit/JellyImageRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java
  - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmo/viewmodel/AgedEmoViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmoImage/viewmodel/AgedEmoImageViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/viewmodel/BasicEmoViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/viewmodel/JellyViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/viewmodel/JellyCombViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyImage/viewmodel/JellyImageViewModel.java
autonomous: true
requirements:
  - QUAL-05

must_haves:
  truths:
    - "JellyDrawerActivity의 userId = 1L 하드코딩이 SessionManager.getUserId()로 교체된다"
    - "모든 Repository가 Context를 받아 RetrofitClient.createService(Class, Context)를 호출한다"
    - "401 응답 시 SessionManager.clear() + RetrofitClient.reset() 후 LoginActivity로 이동한다"
  artifacts:
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java"
      provides: "userId = SessionManager.getUserId() 사용"
  key_links:
    - from: "AuthInterceptor.intercept()"
      to: "401 응답 감지"
      via: "response.code() == 401 체크 후 앱 컨텍스트로 LoginActivity 인텐트"
---

<objective>
JellyDrawerActivity의 하드코딩된 userId = 1L을 SessionManager.getUserId()로 교체하고,
모든 Repository를 Context 기반 RetrofitClient로 업데이트하며,
AuthInterceptor에 401 감지 + 자동 로그아웃 처리를 추가한다.

Purpose: QUAL-05(userId 하드코딩 제거) 요구사항 완성. Phase 1 최종 마무리.
Output: JellyDrawerActivity(수정), 6개 Repository(수정), AuthInterceptor(수정)
</objective>

<execution_context>
@$HOME/.claude/get-shit-done/workflows/execute-plan.md
@$HOME/.claude/get-shit-done/templates/summary.md
</execution_context>

<context>
@.planning/ROADMAP.md
@.planning/phases/01-auth/01-CONTEXT.md
@.planning/phases/01-auth/01-A-SUMMARY.md
@.planning/phases/01-auth/01-B-SUMMARY.md
@.planning/phases/01-auth/01-C-SUMMARY.md

<interfaces>
From SessionManager.java (Plan A):
  public static SessionManager getInstance(Context context)
  public Long getUserId()   -- JWT payload에서 파싱한 userId, 미로그인 시 null
  public void clear()       -- 토큰 삭제

From RetrofitClient.java (Plan B):
  public static <T> T createService(Class<T> serviceClass, Context context)
  public static void reset()  -- 싱글턴 재초기화

Repositories requiring Context update (현재 createService(XxxService.class) 사용):
  AgedEmoRepository, AgedEmoImageRepository, BasicEmoRepository,
  JellyRepository, JellyCombRepository, JellyImageRepository
  (UserRepository는 Plan C에서 이미 수정됨)

JellyDrawerActivity line 49:
  Long userId = 1L;  <-- 제거 대상
</interfaces>
</context>

<tasks>

<task type="auto">
  <name>Task 1: 6개 Repository Context 파라미터 추가</name>
  <files>
    app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmo/retrofit/AgedEmoRepository.java
    app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmoImage/retrofit/AgedEmoImageRepository.java
    app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/retrofit/BasicEmoRepository.java
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyRepository.java
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombRepository.java
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyImage/retrofit/JellyImageRepository.java
    app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmo/viewmodel/AgedEmoViewModel.java
    app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmoImage/viewmodel/AgedEmoImageViewModel.java
    app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/viewmodel/BasicEmoViewModel.java
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/viewmodel/JellyViewModel.java
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/viewmodel/JellyCombViewModel.java
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyImage/viewmodel/JellyImageViewModel.java
  </files>
  <action>
    **Part A — 6개 Repository Context 파라미터 추가:**
    위 6개 Repository 각각에 동일한 패턴 적용:
    1. 생성자에 Context context 파라미터 추가 및 필드 저장.
    2. createService(XxxService.class) -> createService(XxxService.class, context) 로 변경.
    3. import android.content.Context 추가.

    **Part B — 6개 ViewModel AndroidViewModel 전환 (컴파일 오류 방지 필수):**
    각 Repository에 대응하는 ViewModel을 다음 패턴으로 수정:
    1. extends ViewModel → extends AndroidViewModel 로 변경.
    2. 기본 생성자 → public XxxViewModel(Application application) 생성자로 변경.
    3. Repository 생성 시 new XxxRepository(application.getApplicationContext()) 로 전달.
    4. import androidx.lifecycle.AndroidViewModel, android.app.Application 추가.
    대상 ViewModel 6개:
    - AgedEmoViewModel (AgedEmoRepository 사용)
    - AgedEmoImageViewModel (AgedEmoImageRepository 사용)
    - BasicEmoViewModel (BasicEmoRepository 사용)
    - JellyViewModel (JellyRepository 사용)
    - JellyCombViewModel (JellyCombRepository 사용)
    - JellyImageViewModel (JellyImageRepository 사용)
    기존 프로젝트 패턴(No DI) 준수 — 직접 생성 유지.
    AndroidViewModel을 사용하면 ViewModelProvider가 Application을 자동 주입하므로
    Activity 코드 변경 불필요.
  </action>
  <verify>
    <automated>grep -rn "createService.*class)" app/src/main/java/com/mindJellyProject/mindjelly --include="*Repository.java" | grep -v "UserRepository"</automated>
  </verify>
  <done>
    6개 Repository에서 Context 없는 createService() 호출이 0개.
    모든 Repository 생성자에 Context 파라미터 존재.
  </done>
</task>

<task type="auto">
  <name>Task 2: JellyDrawerActivity userId 하드코딩 제거 + AuthInterceptor 401 처리</name>
  <files>
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java
    app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java
  </files>
  <action>
    JellyDrawerActivity.java 수정:
    1. line 49의 Long userId = 1L; 를 제거한다.
    2. SessionManager sm = SessionManager.getInstance(this); 로 교체.
    3. Long userId = sm.getUserId(); 로 교체.
    4. userId가 null인 경우(비로그인 상태): Toast "로그인이 필요합니다." 후 LoginActivity로 이동.

    AuthInterceptor.java 수정 — 401 자동 로그아웃 처리:
    1. 생성자에 Context appContext 파라미터 추가 (Application context — 메모리 누수 방지).
    2. intercept()에서 Response response = chain.proceed(request) 후
       response.code() == 401 이면:
         SessionManager.getInstance(appContext).clear();
         RetrofitClient.reset();
         Intent intent = new Intent(appContext, LoginActivity.class);
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         appContext.startActivity(intent);
       어느 분기든 return response; 로 Response를 반환한다 (OkHttp 인터셉터 계약).
    3. RetrofitClient.getInstance(Context)에서 new AuthInterceptor(context.getApplicationContext())로 생성.
    4. import: android.content.Context, android.content.Intent,
       com.mindJellyProject.mindjelly.users.view.LoginActivity.
  </action>
  <verify>
    <automated>grep -n "getUserId|1L" app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java</automated>
  </verify>
  <done>
    JellyDrawerActivity에 "1L" 하드코딩 없음. getUserId() 호출 존재.
    AuthInterceptor에 response.code() == 401 분기 존재.
  </done>
</task>

</tasks>

<threat_model>
## Trust Boundaries

| Boundary | Description |
|----------|-------------|
| AuthInterceptor -> 401 감지 | 서버 인증 실패 신호를 클라이언트 상태 변경에 사용 |
| JellyDrawerActivity -> SessionManager | JWT에서 추출한 userId로 API 호출 |

## STRIDE Threat Register

| Threat ID | Category | Component | Disposition | Mitigation Plan |
|-----------|----------|-----------|-------------|-----------------|
| T-01E-01 | Elevation of Privilege | userId 하드코딩 제거 | mitigate | SessionManager.getUserId() 사용, JWT 서명 검증은 서버에서 수행 |
| T-01E-02 | Spoofing | AuthInterceptor 401 처리 | mitigate | clear() + reset() 후 FLAG_ACTIVITY_CLEAR_TASK로 백스택 완전 제거 |
</threat_model>

<verification>
1. JellyDrawerActivity에 1L 없음: grep -c "= 1L" ...JellyDrawerActivity.java (0 이어야 함)
2. getUserId() 호출: grep -c "getUserId" ...JellyDrawerActivity.java
3. AuthInterceptor 401: grep -c "401" ...AuthInterceptor.java
4. 전체 Repository Context 확인: grep -rn "createService.*class)" ...--include="*Repository.java" | wc -l (0 이어야 함)
</verification>

<success_criteria>
- JellyDrawerActivity에서 userId = 1L 하드코딩이 완전히 제거되었다
- 모든 Repository가 Context 파라미터를 받아 RetrofitClient에 전달한다
- 401 응답 시 자동으로 세션이 클리어되고 LoginActivity로 이동한다
- Gradle 빌드가 컴파일 에러 없이 성공한다
</success_criteria>

<output>
완료 후 .planning/phases/01-auth/01-E-SUMMARY.md 생성.
</output>
