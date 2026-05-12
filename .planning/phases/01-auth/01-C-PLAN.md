---
phase: 01-auth
plan: C
type: execute
wave: 3
depends_on:
  - 01-A
  - 01-B
files_modified:
  - app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java
  - app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java
  - app/src/main/res/xml/network_security_config.xml
  - app/src/main/AndroidManifest.xml
  - app/src/main/java/com/mindJellyProject/mindjelly/users/retrofit/UserRepository.java
autonomous: true
requirements:
  - AUTH-02
  - AUTH-03

must_haves:
  truths:
    - "로그인 성공 시 JWT가 SessionManager에 저장되고 MainActivity로 이동한다"
    - "이메일 인증 미완료 응답 시 이메일 인증이 필요합니다 메시지가 표시된다 (per D-02)"
    - "일반 로그인 실패 시 이메일 혹은 비밀번호가 틀렸습니다 메시지가 표시된다"
    - "앱 재시작 시 토큰 있으면 MainActivity, 없으면 LoginActivity로 라우팅된다"
    - "cleartext HTTP가 debug 빌드에만 허용된다"
  artifacts:
    - path: "app/src/main/res/xml/network_security_config.xml"
      provides: "debug 전용 cleartext 허용 설정"
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java"
      provides: "토큰 유무에 따른 Login vs Main 라우팅"
  key_links:
    - from: "LoginActivity.observeLogin()"
      to: "SessionManager.saveToken()"
      via: "Resource.success() 분기"
    - from: "SplashActivity"
      to: "SessionManager.hasToken()"
      via: "SessionManager.getInstance(this)"
---

<objective>
LoginActivity에 JWT 저장 + 에러 메시지 세분화를 구현하고,
SplashActivity에 세션 기반 라우팅을 추가하며,
network_security_config.xml로 cleartext를 debug 빌드에만 허용한다.

Purpose: AUTH-02(이메일 인증 UX), AUTH-03(세션 유지) 요구사항 완성.
Output: LoginActivity(수정), SplashActivity(수정), network_security_config.xml(신규), AndroidManifest(수정), UserRepository(수정)
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

<interfaces>
From SessionManager.java (Plan A):
  public static SessionManager getInstance(Context context)
  public void saveToken(String token)
  public String getToken()
  public boolean hasToken()
  public void clear()

From RetrofitClient.java (Plan B):
  public static RetrofitClient getInstance(Context context)
  public static <T> T createService(Class<T> serviceClass, Context context)
  public static void reset()

From UserRepository.java (현재):
  public LiveData<Resource<String>> login(UserLoginReqDTO reqDTO)
  -- Resource.success(String jwt) 또는 Resource.error(String msg) 반환
  -- createService(UserService.class) 호출 -> Context 파라미터 추가 대상

From LoginActivity.java (현재):
  -- login 성공 시 MainActivity 이동만 있음, JWT 저장 없음
  -- 에러 메시지 항상 고정: "이메일 혹은 비밀번호가 틀렸습니다."
</interfaces>
</context>

<tasks>

<task type="auto">
  <name>Task 1: network_security_config.xml 생성 + AndroidManifest 업데이트</name>
  <files>
    app/src/main/res/xml/network_security_config.xml
    app/src/main/AndroidManifest.xml
  </files>
  <action>
    1. app/src/main/res/xml/network_security_config.xml 신규 생성:
       debug 설정 블록에서만 cleartext 허용하고 release는 기본값(cleartext 불허).
       구조: <network-security-config><debug-overrides><trust-anchors>
       + <domain-config cleartextTrafficPermitted="true"><domain>10.0.2.2</domain></domain-config>
       </debug-overrides></network-security-config>
    2. AndroidManifest.xml 수정:
       <application> 태그에서 android:usesCleartextTraffic="true" 속성을 제거하고
       android:networkSecurityConfig="@xml/network_security_config" 속성을 추가한다.
       나머지 기존 내용은 그대로 유지.
  </action>
  <verify>
    <automated>grep -c "networkSecurityConfig" app/src/main/AndroidManifest.xml</automated>
  </verify>
  <done>
    AndroidManifest에 usesCleartextTraffic 제거, networkSecurityConfig 추가.
    network_security_config.xml 존재, debug-overrides 블록 포함.
  </done>
</task>
<task type="auto">
  <name>Task 2: LoginActivity JWT 저장 + 에러 메시지 세분화 (D-02 구현)</name>
  <files>
    app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java
    app/src/main/java/com/mindJellyProject/mindjelly/users/retrofit/UserRepository.java
  </files>
  <action>
    LoginActivity.java 수정:
    1. onCreate()에서 SessionManager.getInstance(this)를 멤버 변수로 초기화.
    2. 로그인 옵저버에서 resource.isSuccess() 분기:
       - 성공(data != null): sessionManager.saveToken(resource.getData()) 호출 후 MainActivity 이동.
       - 실패: resource.getMessage()를 검사하여
         메시지에 "EMAIL_NOT_VERIFIED" 또는 "이메일 인증" 포함 시 -> "이메일 인증이 필요합니다" Toast (per D-02)
         그 외 -> "이메일 혹은 비밀번호가 틀렸습니다." Toast (기존 메시지 유지).
    서버가 어떤 필드로 이메일 인증 미완료를 표시하는지 확정되지 않으므로,
    resource.getMessage() null-safe 처리 필수 (null이면 일반 실패 메시지).

    UserRepository.java 수정:
    login() 내부의 createService(UserService.class) 호출을
    createService(UserService.class, context) 로 변경.
    생성자에 Context context 파라미터 추가 및 필드 저장.
    기존 다른 메서드들(createUser, updateUser 등)도 동일하게 Context 파라미터로 변경.
  </action>
  <verify>
    <automated>grep -n "saveToken|EMAIL_NOT_VERIFIED|이메일 인증이 필요" app/src/main/java/com/mindJellyProject/mindjelly/users/view/LoginActivity.java</automated>
  </verify>
  <done>
    LoginActivity에 saveToken() 호출 존재.
    이메일 인증 미완료 분기 메시지 "이메일 인증이 필요합니다" 존재.
    UserRepository 생성자에 Context 파라미터 추가.
  </done>
</task>
<task type="auto">
  <name>Task 3: SplashActivity 세션 기반 라우팅</name>
  <files>app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java</files>
  <action>
    SplashActivity.java의 postDelayed 람다 내부를 수정한다.
    현재: 항상 LoginActivity로 이동.
    변경 후:
      SessionManager sm = SessionManager.getInstance(SplashActivity.this);
      if (sm.hasToken()) {
          startActivity(new Intent(SplashActivity.this, MainActivity.class));
      } else {
          startActivity(new Intent(SplashActivity.this, LoginActivity.class));
      }
      finish();
    만료 토큰 처리: 별도 서버 검증 API 콜 없음.
    토큰 존재 여부만 체크 후 이동, 만료 토큰은 첫 인증된 API 실패(401) 시 Plan E에서 처리.
    2000ms 딜레이는 그대로 유지.
  </action>
  <verify>
    <automated>grep -n "hasToken|MainActivity|LoginActivity" app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java</automated>
  </verify>
  <done>
    SplashActivity에 hasToken() 분기 존재.
    토큰 있으면 MainActivity, 없으면 LoginActivity로 이동하는 코드 확인.
  </done>
</task>

</tasks>

<threat_model>
## Trust Boundaries

| Boundary | Description |
|----------|-------------|
| LoginActivity -> SessionManager | JWT가 EncryptedSharedPreferences에 저장됨 |
| SplashActivity -> SessionManager | 토큰 유무로 라우팅 결정 |
| HTTP cleartext (debug only) | network_security_config로 범위 제한 |

## STRIDE Threat Register

| Threat ID | Category | Component | Disposition | Mitigation Plan |
|-----------|----------|-----------|-------------|-----------------|
| T-01C-01 | Information Disclosure | LoginActivity observer | mitigate | JWT를 로그캣에 출력하지 않음. saveToken() 이후 데이터 변수 참조 해제. |
| T-01C-02 | Spoofing | SplashActivity routing | accept | 토큰 위조는 Android Keystore 기반 EncryptedSharedPreferences로 방지됨 (Plan A) |
| T-01C-03 | Information Disclosure | cleartext HTTP | mitigate | network_security_config.xml로 debug 빌드에만 제한, release는 HTTPS 강제 |
</threat_model>

<verification>
1. network_security_config.xml 존재: ls app/src/main/res/xml/network_security_config.xml
2. AndroidManifest에 usesCleartextTraffic 제거: grep -c "usesCleartextTraffic" app/src/main/AndroidManifest.xml (0 이어야 함)
3. LoginActivity saveToken 존재: grep -c "saveToken" ...LoginActivity.java
4. 이메일 인증 메시지 존재: grep -c "이메일 인증이 필요" ...LoginActivity.java
5. SplashActivity hasToken 분기: grep -c "hasToken" ...SplashActivity.java
</verification>

<success_criteria>
- 로그인 성공 시 JWT가 SessionManager에 저장되고 MainActivity로 이동한다
- 이메일 인증 미완료 에러 시 "이메일 인증이 필요합니다" 메시지가 표시된다 (per D-02)
- 앱 재시작 시 토큰 유무에 따라 올바른 화면으로 라우팅된다
- cleartext HTTP가 debug 빌드에만 허용된다
</success_criteria>

<output>
완료 후 .planning/phases/01-auth/01-C-SUMMARY.md 생성.
</output>
