---
phase: 01-auth
plan: D
type: execute
wave: 2
depends_on:
  - 01-A
  - 01-B
files_modified:
  - app/src/main/java/com/mindJellyProject/mindjelly/users/view/SignUpActivity.java
autonomous: true
requirements:
  - AUTH-01
  - AUTH-02

must_haves:
  truths:
    - "회원가입 성공 시 Toast로 이메일 인증 안내 후 LoginActivity로 이동한다 (per D-01)"
    - "회원가입 실패 시 한국어 Toast 에러 메시지가 표시된다"
    - "SignUpActivity가 createUser() 결과를 옵저빙하여 성공/실패를 처리한다"
  artifacts:
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/users/view/SignUpActivity.java"
      provides: "회원가입 완성 — createUser() observe + 성공/실패 핸들링"
  key_links:
    - from: "SignUpActivity.createUser() observer"
      to: "LoginActivity"
      via: "Resource.success() 시 Intent + finish()"
---

<objective>
SignUpActivity.java의 createUser() 결과 옵저빙 로직을 완성하여
회원가입 성공 시 이메일 안내 Toast + LoginActivity 이동,
실패 시 한국어 에러 메시지를 표시한다.

Purpose: AUTH-01(회원가입), AUTH-02(이메일 인증 UX D-01) 요구사항 완성.
Output: SignUpActivity.java (수정)
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
From UserRepository.java (Plan C 수정 후):
  public UserRepository(Context context)
  public LiveData<Resource<Users>> createUser(UserSaveReqDTO userSaveReqDTO)
  -- Resource.success(Users) 또는 Resource.error(String) 반환

From UserSaveReqDTO.java (기존):
  기존 필드 확인 후 SignUpActivity UI 필드와 매핑.
  (Executor: SignUpActivity 현재 코드와 DTO 필드를 직접 확인하여 매핑)
</interfaces>
</context>

<tasks>

<task type="auto">
  <name>Task 1: SignUpActivity createUser() 결과 옵저빙 완성 (D-01 구현)</name>
  <files>app/src/main/java/com/mindJellyProject/mindjelly/users/view/SignUpActivity.java</files>
  <action>
    현재 SignUpActivity.registerUser()는 userViewModel.createUser(userSaveReqDTO)를 호출하지만
    결과를 옵저빙하지 않는다. 다음과 같이 완성한다:

    1. 불필요한 import 제거: UserService, retrofit2.Call, retrofit2.Callback, retrofit2.Response
       (직접 Retrofit 호출 제거, ViewModel 패턴으로 통일).

    2. registerUser() 메서드에서 userViewModel.createUser() 반환값을 observe한다:
       userViewModel.createUser(userSaveReqDTO).observe(this, resource -> {
           if (resource != null && resource.getData() != null) {
               // per D-01: Toast 안내 후 LoginActivity로 이동
               Toast.makeText(this, "발송된 이메일을 확인하세요.", Toast.LENGTH_LONG).show();
               Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(intent);
               finish();
           } else {
               String msg = (resource != null && resource.getMessage() != null)
                   ? resource.getMessage() : "회원가입에 실패했습니다.";
               Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
           }
       });

    3. UserViewModel.createUser()가 LiveData<Resource<Users>>를 반환하는지 확인.
       반환 타입이 void라면 LiveData<Resource<Users>>로 변경 필요.
       (현재 UserRepository.createUser()는 LiveData<Resource<Users>> 반환 — ViewModel도 동일하게 래핑)

    4. Intent import 추가: android.content.Intent
  </action>
  <verify>
    <automated>grep -n "발송된 이메일|observe|LoginActivity" app/src/main/java/com/mindJellyProject/mindjelly/users/view/SignUpActivity.java</automated>
  </verify>
  <done>
    SignUpActivity에 createUser() observe 블록 존재.
    성공 시 "발송된 이메일을 확인하세요." Toast + LoginActivity 이동 코드 확인 (per D-01).
  </done>
</task>

</tasks>

<threat_model>
## Trust Boundaries

| Boundary | Description |
|----------|-------------|
| SignUpActivity -> UserRepository | 사용자 입력 데이터가 서버로 전송됨 |

## STRIDE Threat Register

| Threat ID | Category | Component | Disposition | Mitigation Plan |
|-----------|----------|-----------|-------------|-----------------|
| T-01D-01 | Tampering | SignUpActivity 입력값 | accept | 서버에서 이메일 형식 검증 및 중복 체크 수행, 클라이언트는 빈 필드만 기본 확인 |
| T-01D-02 | Information Disclosure | 회원가입 에러 메시지 | accept | 서버 에러 메시지를 그대로 노출하지만 인증 전 단계라 민감 정보 없음 |
</threat_model>

<verification>
1. observe 블록 존재: grep -c "observe" ...SignUpActivity.java
2. D-01 Toast 메시지: grep -c "발송된 이메일" ...SignUpActivity.java
3. LoginActivity 이동: grep -c "LoginActivity" ...SignUpActivity.java
</verification>

<success_criteria>
- 회원가입 성공 시 "발송된 이메일을 확인하세요." Toast 후 LoginActivity로 이동한다 (per D-01)
- 회원가입 실패 시 서버 에러 메시지 또는 기본 한국어 메시지가 표시된다
- 직접 Retrofit 호출 import가 제거되고 ViewModel 패턴만 사용한다
</success_criteria>

<output>
완료 후 .planning/phases/01-auth/01-D-SUMMARY.md 생성.
</output>
