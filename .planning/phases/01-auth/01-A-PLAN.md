---
phase: 01-auth
plan: A
type: execute
wave: 1
depends_on: []
files_modified:
  - gradle/libs.versions.toml
  - app/build.gradle
  - app/src/main/java/com/mindJellyProject/mindjelly/common/SessionManager.java
autonomous: true
requirements:
  - AUTH-03
  - QUAL-05

must_haves:
  truths:
    - "앱 재시작 후에도 JWT 토큰이 EncryptedSharedPreferences에서 복원된다"
    - "SessionManager.getToken()이 저장된 토큰 문자열을 반환한다"
    - "SessionManager.getUserId()가 JWT payload Base64 디코딩에서 userId Long 값을 반환한다"
    - "SessionManager.hasToken()이 토큰 존재 여부를 boolean으로 반환한다"
    - "SessionManager.clear()가 토큰과 userId를 모두 삭제한다"
  artifacts:
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/common/SessionManager.java"
      provides: "JWT 저장/조회/삭제 + userId 추출"
      exports: ["getInstance(Context)","saveToken(String)","getToken()","getUserId()","hasToken()","clear()"]
    - path: "gradle/libs.versions.toml"
      contains: "security-crypto"
    - path: "app/build.gradle"
      contains: "security.crypto"
  key_links:
    - from: "SessionManager.getUserId()"
      to: "JWT payload"
      via: "android.util.Base64 디코딩 후 JSONObject 파싱"
---

## Phase Goal

**As a** MindJelly 사용자, **I want to** 앱을 재시작해도 로그인 상태가 유지되기를, **so that** 매번 다시 로그인하지 않아도 된다.

<objective>
SessionManager.java를 신규 생성하고 EncryptedSharedPreferences 기반 JWT 저장소를 구축한다.
AuthInterceptor(Plan B), LoginActivity 완성(Plan C), userId 제거(Plan E) 모두가 이 파일에 의존한다.

Purpose: JWT 토큰을 암호화 저장하고 payload에서 userId를 추출하는 단일 진입점 제공
Output: SessionManager.java (신규), gradle 의존성 추가
</objective>

<execution_context>
@$HOME/.claude/get-shit-done/workflows/execute-plan.md
@$HOME/.claude/get-shit-done/templates/summary.md
</execution_context>

<context>
@.planning/PROJECT.md
@.planning/ROADMAP.md
@.planning/phases/01-auth/01-CONTEXT.md

<interfaces>
From Resource.java: success(T data), error(String error), getData(), getError()
libs.versions.toml: securityCrypto = "1.1.0-alpha06" 추가 필요
build.gradle: implementation libs.security.crypto 추가 필요
</interfaces>
</context>

<tasks>

<task type="auto">
  <name>Task 1: security-crypto 의존성 추가</name>
  <files>gradle/libs.versions.toml, app/build.gradle</files>
  <read_first>
    - gradle/libs.versions.toml -- [versions], [libraries] 섹션 구조 확인
    - app/build.gradle -- dependencies 블록 확인
  </read_first>
  <action>
    libs.versions.toml [versions]에 추가: securityCrypto = "1.1.0-alpha06"
    libs.versions.toml [libraries]에 추가: security-crypto = { group = "androidx.security", name = "security-crypto", version.ref = "securityCrypto" }
    build.gradle dependencies에 추가: implementation libs.security.crypto
    주의: 하이픈(security-crypto)은 build.gradle에서 점(security.crypto)으로 접근한다.
    minSdk 31이므로 alpha06 호환성 문제 없음 (minSdk 23+ 요구).
  </action>
  <verify>
    libs.versions.toml에 "securityCrypto" 포함.
    libs.versions.toml에 "androidx.security" 포함.
    build.gradle에 "security.crypto" 포함.
  </verify>
  <acceptance_criteria>
    - libs.versions.toml [versions]에 securityCrypto = "1.1.0-alpha06" 라인 존재
    - libs.versions.toml [libraries]에 androidx.security security-crypto 라인 존재
    - build.gradle에 implementation libs.security.crypto 라인 존재
  </acceptance_criteria>
  <done>Gradle sync 후 EncryptedSharedPreferences 임포트가 컴파일 오류 없이 해결된다</done>
</task>

<task type="auto">
  <name>Task 2: SessionManager.java 신규 생성</name>
  <files>app/src/main/java/com/mindJellyProject/mindjelly/common/SessionManager.java</files>
  <read_first>
    - app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java -- 기존 패턴 참고
    - app/src/main/java/com/mindJellyProject/mindjelly/common/Resource.java -- 패키지 위치 확인
  </read_first>
  <action>
    패키지: com.mindJellyProject.mindjelly.common
    클래스: SessionManager (싱글턴)
    상수: PREF_NAME="mindjelly_session", KEY_TOKEN="jwt_token", KEY_USER_ID="user_id"
    필드: private static SessionManager instance; private final SharedPreferences encryptedPrefs

    생성자(private, Context): MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
      .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()으로 masterKey 생성.
      EncryptedSharedPreferences.create(context, PREF_NAME, masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
      GeneralSecurityException/IOException -> RuntimeException으로 throw.

    getInstance(Context): context.getApplicationContext() 사용, null이면 new SessionManager(appContext).
    saveToken(String token): putString(KEY_TOKEN,token).apply() 후 extractAndSaveUserId(token).
    extractAndSaveUserId(String token):
      token.split(".")[1]로 payload 추출.
      Base64.decode(payload, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP)로 디코딩.
      new JSONObject(new String(decoded, StandardCharsets.UTF_8)).getLong("userId")로 추출.
      putLong(KEY_USER_ID, userId).apply() 저장.
      JSONException시 Log.e("SessionManager","userId 추출 실패",e) 기록 후 저장 생략.
    getToken(): getString(KEY_TOKEN,null). getUserId(): getLong(KEY_USER_ID,-1L).
    hasToken(): getToken()!=null. clear(): remove(KEY_TOKEN).remove(KEY_USER_ID).apply().
    임포트: EncryptedSharedPreferences, MasterKey, Context, SharedPreferences,
      Base64, Log, JSONException, JSONObject, StandardCharsets, IOException, GeneralSecurityException
  </action>
  <verify>
    SessionManager.java 존재. "EncryptedSharedPreferences" 포함. "getUserId" 포함.
    "hasToken" 포함. "Base64" 포함. "clear()" 포함.
  </verify>
  <acceptance_criteria>
    - getInstance(Context): getApplicationContext() 사용, null check 싱글턴
    - saveToken(String): EncryptedSharedPreferences 저장 + extractAndSaveUserId 호출
    - extractAndSaveUserId: Base64.URL_SAFE|NO_PADDING|NO_WRAP, JSONObject.getLong("userId"), JSONException catch
    - getUserId(): long 반환, 기본값 -1L
    - hasToken(): getToken()!=null. clear(): KEY_TOKEN+KEY_USER_ID 모두 remove
  </acceptance_criteria>
  <done>saveToken(jwt) 후 getToken()=jwt, hasToken()=true. clear() 후 getToken()=null.</done>
</task>

</tasks>

<threat_model>
## Trust Boundaries

| Boundary | Description |
|----------|-------------|
| JWT->Keystore | EncryptedSharedPreferences + AES256_GCM |
| JWT payload parsing | 파싱 오류 방어 처리 |

## STRIDE Threat Register

| Threat ID | Category | Component | Disposition | Mitigation |
|-----------|----------|-----------|-------------|------------|
| T-01A-01 | Information Disclosure | SharedPrefs JWT | mitigate | EncryptedSharedPreferences AES256_GCM |
| T-01A-02 | Tampering | JWT payload | accept | 서버 서명 검증은 서버에서 수행 |
| T-01A-03 | Info Disclosure | userId 로그 | mitigate | userId 값 자체 로그 미출력 |
</threat_model>

<verification>
1. libs.versions.toml securityCrypto = "1.1.0-alpha06" 존재 확인
2. build.gradle implementation libs.security.crypto 존재 확인
3. SessionManager.java 6개 공개 메서드 존재 확인
4. EncryptedSharedPreferences 사용, 평문 SharedPreferences 미사용 확인
5. Base64.URL_SAFE 플래그 사용 확인
</verification>

<success_criteria>
- security-crypto 1.1.0-alpha06 의존성 gradle 선언
- SessionManager Application Context 기반 초기화
- saveToken() -> EncryptedSharedPreferences 저장 + userId 추출
- getUserId() long 반환 (JWT userId 필드 기반)
- clear() 토큰+userId 동시 삭제
</success_criteria>

<output>
완료 후 .planning/phases/01-auth/01-A-SUMMARY.md 생성
</output>
