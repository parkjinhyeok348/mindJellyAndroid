---
phase: 02-핵심루프
plan: A
type: execute
wave: 1
depends_on: []
files_modified:
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyDrawerResDTO.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyStartAgingReqDTO.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyService.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/viewmodel/JellyViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombService.java
  - app/src/main/java/com/mindJellyProject/mindjelly/common/ErrorMessageUtil.java
  - app/src/main/res/values/colors.xml
  - app/src/main/res/values/strings.xml
  - app/src/main/res/drawable/bg_edit_text.xml
  - app/src/main/res/drawable/bg_status_badge.xml
autonomous: true
requirements:
  - DRAW-01
  - DRAW-02
  - DRAW-03
  - QUAL-01
  - QUAL-02

must_haves:
  truths:
    - "JellyDrawerResDTO에 emo1Name, emo1Icon, emo2Name, emo2Icon, status 필드가 존재한다"
    - "JellyService에 PATCH /api/jelly/{jellyId} 엔드포인트가 선언되어 있다"
    - "JellyViewModel에 startAging(Long jellyId)와 MutableLiveData<Boolean> isLoading이 존재한다"
    - "JellyCombService에 GET /jellyComb/jelly-comb-id/{firstEmo}/{secondEmo} 엔드포인트가 선언되어 있다"
    - "ErrorMessageUtil.getKoreanMessage(Resource<?> resource, Context context)가 HTTP 코드별 한국어 문자열을 반환한다"
    - "colors.xml에 13개 신규 색상 항목이 정의되어 있다"
    - "strings.xml에 21개 신규 문자열 항목이 정의되어 있다"
    - "bg_edit_text.xml, bg_status_badge.xml drawable이 존재한다"
  artifacts:
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyDrawerResDTO.java"
      provides: "젤리서랍 응답 DTO (감정 필드 + status 포함)"
      contains: "emo1Icon, emo2Icon, status"
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyStartAgingReqDTO.java"
      provides: "숙성시작 요청 DTO"
      contains: "status"
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyService.java"
      provides: "Jelly API 인터페이스 (PATCH 포함)"
      contains: "startAging"
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/viewmodel/JellyViewModel.java"
      provides: "JellyViewModel (startAging + isLoading)"
      contains: "isLoading"
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/common/ErrorMessageUtil.java"
      provides: "HTTP 에러 → 한국어 메시지 변환 유틸"
    - path: "app/src/main/res/values/colors.xml"
      provides: "뱃지 3종 + 텍스트 + 서피스 색상 정의"
    - path: "app/src/main/res/values/strings.xml"
      provides: "힌트, CTA, 빈상태, 에러, 성공 문자열 21개"
  key_links:
    - from: "JellyViewModel.startAging(jellyId)"
      to: "JellyRepository.startAging(jellyId)"
      via: "LiveData<Resource<ResponseBody>>"
    - from: "JellyRepository.startAging"
      to: "JellyService.startAging PATCH /api/jelly/{jellyId}"
      via: "enqueue/postValue"
    - from: "ErrorMessageUtil"
      to: "strings.xml error_network / error_server / error_auth / error_not_found / error_generic"
      via: "context.getString(R.string.error_*)"
---

<objective>
Phase 2의 모든 후속 플랜이 의존하는 데이터 계층과 공통 리소스를 완성한다.

Purpose: Wave 2 플랜(B, C)이 컴파일 없이 실행될 수 있도록 DTO 필드, API 인터페이스, ViewModel 메서드, 색상/문자열/드로어블 리소스를 모두 정의한다.
Output: 5개 Java 파일 수정/신규, 2개 XML 리소스 수정, 3개 drawable 신규
</objective>

<execution_context>
@$HOME/.claude/get-shit-done/workflows/execute-plan.md
@$HOME/.claude/get-shit-done/templates/summary.md
</execution_context>

<context>
@.planning/ROADMAP.md
@.planning/phases/02-핵심루프/02-CONTEXT.md
@.planning/phases/02-핵심루프/02-RESEARCH.md
@.planning/phases/02-핵심루프/02-UI-SPEC.md

<interfaces>
<!-- 현재 상태 — 실행자가 이 파일들을 수정한다 -->

From JellyDrawerResDTO.java (현재):
  fields: jellyId(Long), jellyCombId(Long), isAging(Boolean), createDate(String)
  constructor: JellyDrawerResDTO(Long, Long, Boolean, String)
  getters: getJellyId(), getJellyCombId(), getAging(), getCreateDate()
  → 추가 필요: emo1Name(String), emo1Icon(String), emo2Name(String), emo2Icon(String), status(String)

From JellyService.java (현재):
  @POST("/jelly") Call<Jelly> createJelly(@Body JellySaveReqDTO)
  @GET("/jelly/{jellyId}/info") Call<JellyUpdateResDTO> getJellyInfo(@Path Long)
  @PUT("/jelly/{jellyId}") Call<Jelly> updateJelly(@Path Long, @Body JellyUpdateReqDTO)
  @GET("/jelly/{jellyId}") Call<JellyResDTO> getJellyById(@Path Long)
  @GET("jelly/user/{userId}") Call<List<JellyDrawerResDTO>> getJellyList(@Path Long)
  → 추가 필요: @PATCH("/api/jelly/{jellyId}") Call<ResponseBody> startAging(...)

From JellyRepository.java (현재):
  constructor: JellyRepository(Context context)  — RetrofitClient.createService(JellyService.class, context) 사용
  methods: createJelly, getJellyInfo, updateJelly, getJellyById, getJellyList
  pattern: enqueue() → if(isSuccessful()) postValue(Resource.success) else postValue(Resource.error)
  → 추가 필요: startAging(Long jellyId, JellyStartAgingReqDTO reqDTO) → LiveData<Resource<ResponseBody>>

From JellyViewModel.java (현재):
  extends AndroidViewModel
  constructor: JellyViewModel(Application) — new JellyRepository(application.getApplicationContext())
  methods: createJelly, getJellyInfo, updateJelly, getJellyById, getJellyList
  → 추가 필요: public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false)
  → 추가 필요: startAging(Long jellyId) — isLoading.postValue(true) 전, 완료 시 false

From JellyCombService.java (현재):
  @GET("/jellyComb/{jellyCombId}") Call<JellyCombResDTO> getJellyCombById(@Path Long)
  @GET("/jellyComb/jelly-icon/{firstEmo}/{secondEmo}/{isAwaken}") Call<String> getJellyIcon(...)
  → 추가 필요: @GET("/jellyComb/jelly-comb-id/{firstEmo}/{secondEmo}") Call<Long> getJellyCombId(@Path Long, @Path Long)

From Resource.java (현재):
  Resource.success(T data) — data != null → isSuccess() true
  Resource.error(String error) — error != null → isError() true
  getData(), getError(), isSuccess(), isError()

From colors.xml (현재):
  black, white — 2개만 존재

From strings.xml (현재):
  app_name, title_activity_login, prompt_email, prompt_password, action_sign_in,
  action_sign_in_short, welcome, invalid_username, invalid_password, login_failed, jellycombinationimage
  → 21개 신규 항목 추가 필요 (UI-SPEC.md Copywriting Contract 참조)
</interfaces>
</context>

<tasks>

<task type="auto" tdd="true">
  <name>Task A-1: 데이터 모델 확장 — JellyDrawerResDTO + JellyStartAgingReqDTO 신규</name>
  <files>
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyDrawerResDTO.java
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyStartAgingReqDTO.java
  </files>
  <read_first>
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyDrawerResDTO.java (현재 4개 필드 확인)
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellySaveReqDTO.java (필드 선언 패턴 참조)
    - .planning/phases/02-핵심루프/02-CONTEXT.md (D-01, D-02 결정 확인)
    - .planning/phases/02-핵심루프/02-RESEARCH.md (JellyDrawerResDTO 감정 필드 섹션)
  </read_first>
  <behavior>
    - JellyDrawerResDTO: getEmo1Name() 반환 "기쁨" 같은 비null String
    - JellyDrawerResDTO: getEmo1Icon() 반환 "/images/joy.png" 같은 비null String
    - JellyDrawerResDTO: getStatus() 반환 "WAITING" / "AGING" / "MATURED" 중 하나
    - JellyStartAgingReqDTO: new JellyStartAgingReqDTO()의 getStatus() == "AGING"
  </behavior>
  <action>
    JellyDrawerResDTO.java에 다음 5개 필드를 기존 4개 필드 아래에 추가한다 (per D-01, D-02):
    - @SerializedName("emo1Name") private String emo1Name
    - @SerializedName("emo1Icon") private String emo1Icon
    - @SerializedName("emo2Name") private String emo2Name
    - @SerializedName("emo2Icon") private String emo2Icon
    - @SerializedName("status") private String status

    기존 생성자는 유지하고, 5개 새 필드를 받는 생성자를 추가하거나 setter만으로 Gson 역직렬화를 처리해도 된다(Gson은 private 필드를 reflection으로 채우므로 setter 없이도 동작). getter 5개 추가: getEmo1Name(), getEmo1Icon(), getEmo2Name(), getEmo2Icon(), getStatus().

    JellyStartAgingReqDTO.java를 신규 생성한다 — 패키지 com.mindJellyProject.mindjelly.jellyDomain.jelly.model:
    - @SerializedName("status") private String status = "AGING" (per D-03, A5)
    - 기본 생성자 JellyStartAgingReqDTO() — status를 "AGING"으로 초기화
    - getter: getStatus()
  </action>
  <verify>
    <automated>cd c:\Users\KTDS\git\mindJellyAndroid && gradlew.bat compileDebugSources 2>&1 | tail -5</automated>
  </verify>
  <acceptance_criteria>
    - JellyDrawerResDTO.java에 @SerializedName("emo1Icon"), @SerializedName("emo2Icon"), @SerializedName("status") 어노테이션이 존재한다
    - JellyStartAgingReqDTO.java 파일이 com.mindJellyProject.mindjelly.jellyDomain.jelly.model 패키지에 존재한다
    - JellyStartAgingReqDTO의 기본 생성자로 생성한 인스턴스의 getStatus()가 "AGING"을 반환한다
    - ./gradlew compileDebugSources가 BUILD SUCCESSFUL로 종료된다
  </acceptance_criteria>
  <done>JellyDrawerResDTO 5개 신규 필드 완성, JellyStartAgingReqDTO 신규 생성, 컴파일 통과</done>
</task>

<task type="auto" tdd="true">
  <name>Task A-2: API 계층 확장 — JellyService PATCH + JellyRepository/ViewModel startAging + isLoading + JellyCombService jellyCombId 엔드포인트</name>
  <files>
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyService.java
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyRepository.java
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/viewmodel/JellyViewModel.java
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombService.java
  </files>
  <read_first>
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyService.java (현재 엔드포인트 목록)
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyRepository.java (enqueue/postValue 패턴 전체)
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/viewmodel/JellyViewModel.java (현재 메서드 목록)
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombService.java (현재 엔드포인트)
    - .planning/phases/02-핵심루프/02-CONTEXT.md (D-03: PATCH /api/jelly/{id})
    - .planning/phases/02-핵심루프/02-RESEARCH.md (Pattern 1, Pattern 2, Pitfall 1)
  </read_first>
  <behavior>
    - JellyViewModel.isLoading는 MutableLiveData&lt;Boolean&gt;이며 초기값 false
    - JellyViewModel.startAging(jellyId) 호출 시 isLoading.getValue()가 즉시 true가 된다
    - JellyCombService에 getJellyCombId(Long, Long) 메서드가 존재한다
  </behavior>
  <action>
    JellyService.java에 다음을 추가한다 (per D-03):
    - import okhttp3.ResponseBody 및 @PATCH import 추가
    - @PATCH("/api/jelly/{jellyId}") Call&lt;ResponseBody&gt; startAging(@Path("jellyId") Long jellyId, @Body JellyStartAgingReqDTO reqDTO)

    JellyRepository.java에 startAging 메서드를 추가한다. 패턴은 기존 createJelly와 동일(enqueue + postValue):
    - public LiveData&lt;Resource&lt;ResponseBody&gt;&gt; startAging(Long jellyId, JellyStartAgingReqDTO reqDTO)
    - onResponse: isSuccessful() → Resource.success(response.body()), 아니면 Resource.error("Error: " + response.message())
    - onFailure: Resource.error(t.getMessage())

    JellyViewModel.java에 두 가지를 추가한다 (QUAL-01, per RESEARCH.md Pattern 2):
    1. 필드 선언: public final MutableLiveData&lt;Boolean&gt; isLoading = new MutableLiveData&lt;&gt;(false)
       → import androidx.lifecycle.MutableLiveData 추가 필요
    2. 메서드: public LiveData&lt;Resource&lt;ResponseBody&gt;&gt; startAging(Long jellyId)
       - 내부에서 isLoading.postValue(true)를 먼저 호출한다
       - repository.startAging(jellyId, new JellyStartAgingReqDTO())를 호출한다
       - 반환된 LiveData를 옵저빙하지 않고 그대로 return한다
       - 호출자(Activity)가 observe하여 완료 시 isLoading.postValue(false)를 직접 처리하는 방식을 사용한다
       - 대안: repository 호출 결과를 MediatorLiveData로 래핑하여 postValue(false) 후 중계해도 됨
       (두 방식 모두 허용 — 선택은 executor 재량)

    JellyCombService.java에 다음을 추가한다 (Pitfall 1 해결):
    - @GET("/jellyComb/jelly-comb-id/{firstEmo}/{secondEmo}") Call&lt;Long&gt; getJellyCombId(@Path("firstEmo") Long firstEmo, @Path("secondEmo") Long secondEmo)
    - 이 엔드포인트가 백엔드에 없을 경우를 대비해 선언은 추가하되, 실제 호출 전 백엔드 확인이 필요하다는 주석을 달아둔다
  </action>
  <verify>
    <automated>cd c:\Users\KTDS\git\mindJellyAndroid && gradlew.bat compileDebugSources 2>&1 | tail -5</automated>
  </verify>
  <acceptance_criteria>
    - JellyService.java에 @PATCH("/api/jelly/{jellyId}") 어노테이션이 존재한다
    - JellyViewModel.java에 "public final MutableLiveData" 선언이 존재한다
    - JellyViewModel.java에 startAging 메서드가 존재한다
    - JellyCombService.java에 "/jellyComb/jelly-comb-id/" 경로가 존재한다
    - ./gradlew compileDebugSources가 BUILD SUCCESSFUL로 종료된다
  </acceptance_criteria>
  <done>JellyService PATCH 추가, JellyRepository/ViewModel startAging 구현, isLoading 필드 선언, JellyCombService jellyCombId 엔드포인트 추가, 컴파일 통과</done>
</task>

<task type="auto">
  <name>Task A-3: 공통 리소스 — ErrorMessageUtil + colors.xml + strings.xml + drawables</name>
  <files>
    app/src/main/java/com/mindJellyProject/mindjelly/common/ErrorMessageUtil.java
    app/src/main/res/values/colors.xml
    app/src/main/res/values/strings.xml
    app/src/main/res/drawable/bg_edit_text.xml
    app/src/main/res/drawable/bg_status_badge.xml
  </files>
  <read_first>
    - app/src/main/res/values/colors.xml (현재 black/white 2개만 존재)
    - app/src/main/res/values/strings.xml (현재 11개 항목 존재)
    - app/src/main/java/com/mindJellyProject/mindjelly/common/Resource.java (isSuccess(), isError(), getError() API)
    - .planning/phases/02-핵심루프/02-UI-SPEC.md (Color 섹션 전체, Copywriting Contract 전체, bg_edit_text.xml / bg_status_badge.xml 정의)
  </read_first>
  <action>
    ErrorMessageUtil.java를 com.mindJellyProject.mindjelly.common 패키지에 신규 생성한다 (per QUAL-02, RESEARCH.md 권장):
    - public static String getKoreanMessage(Resource&lt;?&gt; resource, Context context) 메서드 하나
    - resource.isError()가 true이면 resource.getError()를 분석하여:
      - getError()가 null 또는 네트워크 실패 키워드(UnknownHostException, timeout, Unable to resolve, No address, SocketTimeoutException)를 포함하면 context.getString(R.string.error_network)
      - 그 외 에러는 HTTP 코드 prefix 패싱: "Error: " 뒤 메시지를 코드 숫자로 파싱. response.code()가 없으므로 기존 에러 문자열에서 code를 직접 얻기 어렵다. 따라서:
        - "401"이 포함되면 R.string.error_auth
        - "404"가 포함되면 R.string.error_not_found
        - "5" 또는 "500"~"599" 숫자가 포함되면 R.string.error_server
        - 나머지 모두 R.string.error_generic
    - resource.isSuccess()이면 null 반환 (성공 토스트는 각 Activity에서 별도 처리)
    - 별도 오버로드: public static String getKoreanMessage(String errorMsg, Context context) — Activity의 onFailure/error 경로에서 직접 errorMsg를 넘길 때 사용

    colors.xml에 UI-SPEC.md Color 섹션의 13개 신규 항목을 기존 black/white 아래에 추가한다:
    text_primary(#FF333333), text_secondary(#FF666666), text_hint(#FF999999), surface_card(#E6FFFFFF), accent_blue(#FF0000FF), badge_waiting_bg(#FFFFEEBA), badge_waiting_text(#FF856404), badge_aging_bg(#FFD1ECF1), badge_aging_text(#FF0C5460), badge_complete_bg(#FFD4EDDA), badge_complete_text(#FF155724), error_red(#FFDC3545), divider_neutral(#FFEEEEEE)

    strings.xml에 UI-SPEC.md Copywriting Contract의 21개 신규 항목을 기존 항목 아래에 추가한다:
    hint_diary_entry, btn_save_jelly, empty_drawer_title, empty_drawer_body, error_network, error_server, error_auth, error_not_found, error_generic, error_save_failed, error_diary_empty, success_aging_started, error_aging_failed, success_jelly_saved, btn_start_aging, status_waiting, status_aging, status_complete, cd_jelly_preview, cd_emotion_icon, cd_emo1_icon, cd_emo2_icon
    (각 항목의 한국어 값은 02-UI-SPEC.md Copywriting Contract 표를 정확히 따른다)

    bg_edit_text.xml을 res/drawable/에 신규 생성한다 — UI-SPEC.md에 정의된 내용:
    shape=rectangle, corners radius=8dp, solid color=#E6FFFFFF

    bg_status_badge.xml을 res/drawable/에 신규 생성한다 — UI-SPEC.md에 정의된 내용:
    shape=rectangle, corners radius=4dp, solid color=#FFFFEEBA (기본/대기중 색상; Adapter에서 GradientDrawable.mutate().setColor()로 상태별 교체)
  </action>
  <verify>
    <automated>cd c:\Users\KTDS\git\mindJellyAndroid && gradlew.bat compileDebugSources 2>&1 | tail -5</automated>
  </verify>
  <acceptance_criteria>
    - ErrorMessageUtil.java가 com.mindJellyProject.mindjelly.common 패키지에 존재한다
    - ErrorMessageUtil.getKoreanMessage(Resource.error("401 Unauthorized"), context) 경로가 R.string.error_auth로 분기된다 (코드 리뷰로 확인)
    - colors.xml에 "badge_waiting_bg", "badge_aging_bg", "badge_complete_bg", "text_hint", "accent_blue" 항목이 모두 존재한다 (grep으로 확인)
    - strings.xml에 "hint_diary_entry", "empty_drawer_title", "btn_save_jelly", "error_network", "success_jelly_saved" 항목이 모두 존재한다 (grep으로 확인)
    - res/drawable/bg_edit_text.xml이 존재하고 android:radius="8dp"가 포함된다
    - res/drawable/bg_status_badge.xml이 존재하고 android:radius="4dp"가 포함된다
    - ./gradlew compileDebugSources가 BUILD SUCCESSFUL로 종료된다
  </acceptance_criteria>
  <done>ErrorMessageUtil 신규, colors.xml 13개 추가, strings.xml 21개 추가, bg_edit_text.xml / bg_status_badge.xml 신규, 컴파일 통과</done>
</task>

</tasks>

<threat_model>
## Trust Boundaries

| Boundary | Description |
|----------|-------------|
| ViewModel → Repository | DTO 필드 추가로 Gson이 null 역직렬화할 수 있는 신규 필드 |
| Activity → ErrorMessageUtil | getError() 문자열이 null일 때 NPE 위험 |

## STRIDE Threat Register

| Threat ID | Category | Component | Disposition | Mitigation Plan |
|-----------|----------|-----------|-------------|-----------------|
| T-02A-01 | Tampering | JellyStartAgingReqDTO.status | mitigate | status 필드를 "AGING"으로 고정하고 setter를 노출하지 않는다 — 클라이언트가 임의 상태로 전환 불가 |
| T-02A-02 | Information Disclosure | ErrorMessageUtil | accept | getError() 내부 메시지는 Toast로 표시되며 백엔드 스택트레이스는 포함되지 않음 — 낮은 위험 |
| T-02A-03 | Denial of Service | JellyViewModel.isLoading | accept | isLoading.postValue는 main thread safe, MutableLiveData 자체 동기화 보장 |
</threat_model>

<verification>
Wave 1 전체 통과 기준:
- ./gradlew compileDebugSources → BUILD SUCCESSFUL
- ./gradlew testDebugUnitTest → BUILD SUCCESSFUL (기존 테스트 회귀 없음)
- JellyDrawerResDTO에 5개 신규 필드 존재 (grep emo1Icon JellyDrawerResDTO.java)
- JellyViewModel에 isLoading 선언 존재 (grep isLoading JellyViewModel.java)
- colors.xml 항목 수: 기존 2 + 신규 13 = 15 이상 (grep -c "color name" colors.xml)
- strings.xml 항목 수: 기존 11 + 신규 21 = 32 이상 (grep -c "string name" strings.xml)
</verification>

<success_criteria>
- JellyDrawerResDTO: emo1Name, emo1Icon, emo2Name, emo2Icon, status 필드 + getter 존재
- JellyStartAgingReqDTO: status="AGING" 기본값 신규 클래스 존재
- JellyService: @PATCH("/api/jelly/{jellyId}") startAging 선언 존재
- JellyRepository: startAging(Long, JellyStartAgingReqDTO) 메서드 존재
- JellyViewModel: isLoading MutableLiveData&lt;Boolean&gt; + startAging(Long) 존재
- JellyCombService: getJellyCombId(Long, Long) 선언 존재
- ErrorMessageUtil: getKoreanMessage 정적 메서드 존재, 401/404/5xx/network 분기
- colors.xml: 13개 신규 색상 (badge 6개, text 3개, surface 1개, accent 1개, error 1개, divider 1개)
- strings.xml: 21개 신규 문자열 항목
- bg_edit_text.xml: radius=8dp, color=#E6FFFFFF
- bg_status_badge.xml: radius=4dp, color=#FFFFEEBA
- 전체 컴파일 통과
</success_criteria>

<output>
완료 후 .planning/phases/02-핵심루프/02-A-SUMMARY.md 생성
</output>
