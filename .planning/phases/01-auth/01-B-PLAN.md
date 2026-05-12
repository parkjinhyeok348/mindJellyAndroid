---
phase: 01-auth
plan: B
type: execute
wave: 2
depends_on:
  - 01-A
files_modified:
  - app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java
  - app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java
autonomous: true
requirements:
  - AUTH-03

must_haves:
  truths:
    - "모든 API 요청 헤더에 Authorization: Bearer <token>이 자동으로 포함된다"
    - "토큰이 없을 때 AuthInterceptor가 헤더 없이 요청을 통과시킨다"
    - "RetrofitClient가 OkHttpClient + AuthInterceptor를 사용하는 싱글턴으로 동작한다"
    - "RetrofitClient.reset()으로 싱글턴을 재초기화할 수 있다"
  artifacts:
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java"
      provides: "OkHttp Bearer 토큰 주입 인터셉터"
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java"
      provides: "OkHttpClient + AuthInterceptor 포함 Retrofit 싱글턴"
  key_links:
    - from: "AuthInterceptor.intercept()"
      to: "SessionManager.getToken()"
      via: "생성자 주입된 appContext로 SessionManager.getInstance(appContext) 호출"
    - from: "RetrofitClient.getInstance(Context)"
      to: "AuthInterceptor"
      via: "OkHttpClient.Builder().addInterceptor(new AuthInterceptor(appContext))"
---

<objective>
AuthInterceptor.java를 신규 생성하고 RetrofitClient.java를 리팩터링하여
모든 API 요청에 JWT Bearer 토큰이 자동으로 주입되도록 한다.

Purpose: Plan A의 SessionManager를 소비하는 첫 클라이언트. 이후 모든 인증된 API 호출의 인프라.
Output: AuthInterceptor.java (신규), RetrofitClient.java (리팩터)
</objective>

<execution_context>
@$HOME/.claude/get-shit-done/workflows/execute-plan.md
@$HOME/.claude/get-shit-done/templates/summary.md
</execution_context>

<context>
@.planning/ROADMAP.md
@.planning/phases/01-auth/01-CONTEXT.md
@.planning/phases/01-auth/01-A-SUMMARY.md

<interfaces>
From SessionManager.java (Plan A 산출물):
  public static SessionManager getInstance(Context context)
  public String getToken()   -- null 가능
  public boolean hasToken()

From RetrofitClient.java (현재 상태):
  private static Retrofit retrofit
  private static final String BASE_URL = "http://10.0.2.2:8080/"
  public static Retrofit getInstance()          -- Context 파라미터 추가 대상
  public static <T> T createService(Class<T>)  -- Context 파라미터 추가 대상
</interfaces>
</context>


<tasks>

<task type="auto">
  <name>Task 1: AuthInterceptor.java 신규 생성</name>
  <files>app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java</files>
  <action>
    com.mindJellyProject.mindjelly.common 패키지에 AuthInterceptor.java를 신규 생성한다.
    - okhttp3.Interceptor를 implements한다.
    - 생성자: public AuthInterceptor(Context appContext) — appContext 필드에 저장.
      (SessionManager는 외부에서 주입하지 않고 내부에서 getInstance(appContext)로 획득.
       Plan E에서 appContext를 사용해 401 처리도 하므로 Context 보관이 필수.)
    - intercept(Chain chain): SessionManager.getInstance(appContext).getToken() 호출.
      - 토큰이 null이 아니면: chain.request().newBuilder().header("Authorization", "Bearer " + token).build()로 새 Request 생성 후 chain.proceed().
      - 토큰이 null이면: 원본 request를 그대로 chain.proceed().
    - import: android.content.Context, okhttp3.Interceptor, okhttp3.Request, okhttp3.Response, java.io.IOException.
    - 401 리다이렉트 처리는 이 클래스에서 하지 않음 — Plan E에서 intercept() 확장.
  </action>
  <verify>
    <automated>grep -n "Bearer" app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java</automated>
  </verify>
  <done>AuthInterceptor.java 파일이 존재하고 "Bearer " + token 헤더 주입 코드를 포함한다.</done>
</task>
<task type="auto">
  <name>Task 2: RetrofitClient.java 리팩터 — OkHttpClient + AuthInterceptor 적용</name>
  <files>app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java</files>
  <action>
    기존 RetrofitClient.java를 다음과 같이 리팩터링한다.
    1. 필드 추가: private static RetrofitClient instance; (인스턴스 싱글턴으로 전환).
    2. 생성자 private RetrofitClient(Context context): SessionManager를 가져와
       OkHttpClient.Builder().addInterceptor(new AuthInterceptor(context.getApplicationContext())).build()로
       OkHttpClient를 생성하고, 이를 Retrofit.Builder().baseUrl(BASE_URL)
       .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()에 전달.
    3. getInstance(Context context): instance가 null이면 new RetrofitClient(context) 생성.
    4. createService(Class<T> serviceClass, Context context): getInstance(context).retrofit.create(serviceClass).
    5. reset(): instance = null — 로그아웃이나 토큰 갱신 시 싱글턴 재초기화용.
    6. 기존 static Retrofit retrofit 필드와 static getInstance()/createService() 시그니처를
       인스턴스 기반으로 교체. 하위 호환 오버로드는 추가하지 말 것.
    import: android.content.Context, okhttp3.OkHttpClient, retrofit2.Retrofit,
    retrofit2.converter.gson.GsonConverterFactory.
  </action>
  <verify>
    <automated>grep -n "addInterceptor|AuthInterceptor|reset" app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java</automated>
  </verify>
  <done>RetrofitClient.java에 AuthInterceptor 추가, Context 파라미터, reset() 메서드가 모두 존재한다.</done>
</task>

</tasks>
<threat_model>
## Trust Boundaries

| Boundary | Description |
|----------|-------------|
| AuthInterceptor -> OkHttp chain | 토큰이 모든 호스트에 전송됨 (현재 단일 서버, 외부 CDN 없음) |

## STRIDE Threat Register

| Threat ID | Category | Component | Disposition | Mitigation Plan |
|-----------|----------|-----------|-------------|-----------------|
| T-01B-01 | Information Disclosure | AuthInterceptor.intercept() | mitigate | HTTPS 전환 전까지 BASE_URL을 10.0.2.2(에뮬레이터 전용)로 제한, cleartext는 debug 빌드에만 허용 (Plan C에서 구현) |
| T-01B-02 | Tampering | RetrofitClient singleton | accept | 싱글톤은 앱 프로세스 내부에서만 접근 가능, Android 앱 샌드박스로 보호됨 |
</threat_model>

<verification>
1. AuthInterceptor.java 파일 존재 확인: ls app/src/main/java/com/mindJellyProject/mindjelly/common/AuthInterceptor.java
2. Bearer 헤더 주입 코드 확인: grep -n "Bearer" ...AuthInterceptor.java
3. RetrofitClient에 addInterceptor 확인: grep -n "addInterceptor" ...RetrofitClient.java
4. RetrofitClient에 reset() 메서드 확인: grep -n "reset" ...RetrofitClient.java
5. RetrofitClient에 Context 파라미터 확인: grep -n "Context" ...RetrofitClient.java
</verification>

<success_criteria>
- AuthInterceptor.java가 생성되고 SessionManager에서 토큰을 읽어 Authorization 헤더를 주입한다
- RetrofitClient.java가 OkHttpClient + AuthInterceptor를 포함하는 인스턴스 기반 싱글턴으로 변경된다
- reset() 메서드가 존재하여 싱글턴 재초기화가 가능하다
- 컴파일 에러 없이 Gradle 빌드가 성공한다
</success_criteria>

<output>
완료 후 .planning/phases/01-auth/01-B-SUMMARY.md 생성.
</output>
