---
phase: 02-핵심루프
plan: D
type: execute
wave: 3
depends_on: [02-B, 02-C]
files_modified:
  - app/src/main/res/layout/activity_jelly_drawer.xml
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java
autonomous: true
requirements:
  - DRAW-03
  - DRAW-04
  - QUAL-01
  - QUAL-02

must_haves:
  truths:
    - "JellyDrawerActivity가 ViewBinding(ActivityJellyDrawerBinding)을 사용한다"
    - "getJellyList 호출 전 isLoading.postValue(true), 완료 후 false가 호출된다"
    - "빈 리스트 시 rvJellyList GONE, tvEmptyState/tvEmptyStateBody VISIBLE, pbLoading GONE"
    - "리스트 존재 시 rvJellyList VISIBLE, tvEmptyState/tvEmptyStateBody GONE, pbLoading GONE"
    - "startAging 성공 시 AgingRoomActivity로 이동하고 success_aging_started Toast가 표시된다"
    - "에러 시 ErrorMessageUtil.getKoreanMessage()로 변환된 한국어 Toast가 표시된다"
  artifacts:
    - path: "app/src/main/res/layout/activity_jelly_drawer.xml"
      provides: "JellyDrawer 레이아웃 — FrameLayout + tvEmptyState + tvEmptyStateBody + pbLoading"
      contains: "rvJellyList, tvEmptyState, tvEmptyStateBody, pbLoading"
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java"
      provides: "JellyDrawerActivity 완성 — ViewBinding + 빈상태 + 로딩 + startAging + 한국어 에러"
  key_links:
    - from: "JellyDrawerActivity.getJellyList observe"
      to: "adapter.submitList(list) / tvEmptyState VISIBLE / pbLoading GONE"
      via: "resource.isSuccess() + list.isEmpty() 분기"
    - from: "adapter.setOnStartAgingListener"
      to: "jellyViewModel.startAging(jellyId).observe"
      via: "OnStartAgingListener 콜백 (Plan C에서 정의된 인터페이스)"
    - from: "jellyViewModel.isLoading"
      to: "pbLoading.setVisibility(VISIBLE/GONE)"
      via: "isLoading.observe"
---

<objective>
JellyDrawerActivity를 ViewBinding으로 전환하고, 빈 상태 UI, 로딩 인디케이터, 숙성시키기 콜백, 한국어 에러 메시지를 완성한다.

Purpose: DRAW-03(숙성시키기 → AgingRoom), DRAW-04(빈 상태), QUAL-01(로딩 인디케이터), QUAL-02(한국어 에러)를 Activity 계층에서 완성한다. Plan C에서 정의된 JellyDrawerAdapter.OnStartAgingListener를 구현하여 startAging API를 연결한다.
Output: activity_jelly_drawer.xml 재구성(FrameLayout 루트), JellyDrawerActivity ViewBinding + 완전한 비즈니스 로직
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
@.planning/phases/02-핵심루프/02-B-SUMMARY.md
@.planning/phases/02-핵심루프/02-C-SUMMARY.md

<interfaces>
<!-- Plan A/C에서 확립된 계약 + 현재 JellyDrawerActivity 상태 -->

From JellyDrawerActivity.java (현재):
  setContentView(R.layout.activity_jelly_drawer)            ← ViewBinding으로 교체
  RecyclerView rvJellyList = findViewById(R.id.rvJellyList) ← binding.rvJellyList로 교체
  adapter = new JellyDrawerAdapter()                         ← JellyDrawerAdapter는 이제 ListAdapter
  jellyViewModel.getJellyList(userId).observe(...)          ← adapter.setJellyList() → adapter.submitList() + 빈 상태 분기 추가
  Long userId = sessionManager.getUserId()                   ← 유지 (autoboxing, == -1L 체크 동작)
  Toast에 "젤리 리스트를 불러오는데 실패했습니다: " + errorMsg  ← ErrorMessageUtil로 교체

From JellyViewModel.java (Plan A 수정 후):
  public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false)
  startAging(Long jellyId) → LiveData<Resource<ResponseBody>>  ← 내부에서 isLoading.postValue(true) 호출

From JellyDrawerAdapter.java (Plan C 수정 후):
  extends ListAdapter<JellyDrawerResDTO, ViewHolder>
  submitList(List<JellyDrawerResDTO>) — 리스트 갱신
  interface OnStartAgingListener { void onStartAging(Long jellyId); }
  setOnStartAgingListener(OnStartAgingListener l)

From AgingRoomActivity.java (기존):
  AgingRoomActivity — 빈 Activity 스텁, 이미 선언됨, 이동 가능
  패키지: com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view.AgingRoomActivity

From ErrorMessageUtil.java (Plan A 신규):
  static String getKoreanMessage(Resource<?> resource, Context context) → 한국어 에러 문자열

From activity_jelly_drawer.xml (현재):
  LinearLayout 루트 (vertical)
    RecyclerView id=rvJellyList — layout_marginTop=273dp, marginBottom=53dp, marginStart=15dp, marginEnd=16dp
  → FrameLayout 루트로 교체, tvEmptyState/tvEmptyStateBody/pbLoading 추가

From Plan A resources:
  @string/empty_drawer_title, @string/empty_drawer_body
  @color/text_hint
  (pbLoading은 새로 추가되는 View이므로 별도 drawable 불필요)

Visibility state machine (UI-SPEC.md Component Contract 3):
  Loading:          rvJellyList GONE  | tvEmptyState GONE    | pbLoading VISIBLE
  Loaded non-empty: rvJellyList VISIBLE | tvEmptyState GONE  | pbLoading GONE
  Loaded empty:     rvJellyList GONE  | tvEmptyState VISIBLE | pbLoading GONE
  Error:            rvJellyList GONE  | tvEmptyState GONE    | pbLoading GONE (Toast 표시)
</interfaces>
</context>

<tasks>

<task type="auto" tdd="true">
  <name>Task D-1: activity_jelly_drawer.xml 재구성 — FrameLayout 루트 + 빈 상태 뷰 + pbLoading</name>
  <files>
    app/src/main/res/layout/activity_jelly_drawer.xml
  </files>
  <read_first>
    - app/src/main/res/layout/activity_jelly_drawer.xml (현재 LinearLayout 루트 + RecyclerView 마진값 확인 — 273dp/53dp 보존 필수)
    - .planning/phases/02-핵심루프/02-UI-SPEC.md (Component Contract 3: activity_jelly_drawer.xml 섹션 전체 — FrameLayout 레이아웃 구조, visibility state machine 표)
    - app/src/main/res/values/strings.xml (Plan A 추가 후 empty_drawer_title, empty_drawer_body 존재 확인)
    - app/src/main/res/values/colors.xml (Plan A 추가 후 text_hint 존재 확인)
  </read_first>
  <behavior>
    - activity_jelly_drawer.xml 루트 태그가 FrameLayout이다
    - id=rvJellyList의 layout_marginTop=273dp, layout_marginBottom=53dp가 유지된다
    - id=tvEmptyState (text=@string/empty_drawer_title, gravity=center, textColor=@color/text_hint, visibility=gone)가 존재한다
    - id=tvEmptyStateBody (text=@string/empty_drawer_body, gravity=center, visibility=gone)가 존재한다
    - id=pbLoading (indeterminate=true, visibility=gone, layout_gravity=center)가 존재한다
    - id=main이 루트 FrameLayout에 존재한다 (ViewCompat.setOnApplyWindowInsetsListener 참조용)
  </behavior>
  <action>
    activity_jelly_drawer.xml을 UI-SPEC.md Component Contract 3에 따라 재작성한다.

    루트: FrameLayout, id=main, layout_width=match_parent, layout_height=match_parent,
          android:background="@drawable/basic_background"

    첫 번째 자식 — RecyclerView id=rvJellyList:
    layout_width=match_parent, layout_height=match_parent
    layout_marginTop=273dp    (배경 이미지 정렬값 — 변경 금지)
    layout_marginBottom=53dp  (배경 이미지 정렬값 — 변경 금지)
    layout_marginStart=16dp   (기존 15dp에서 16dp로 표준화, UI-SPEC sm→lg 토큰)
    layout_marginEnd=16dp
    android:scrollbars=vertical
    android:visibility=gone   (observe에서 토글)

    두 번째 자식 — TextView id=tvEmptyState:
    layout_width=wrap_content, layout_height=wrap_content
    layout_gravity=center
    layout_marginTop=273dp
    android:textSize=14sp
    android:textColor=@color/text_hint
    android:gravity=center
    android:text=@string/empty_drawer_title
    android:visibility=gone

    세 번째 자식 — TextView id=tvEmptyStateBody:
    layout_width=wrap_content, layout_height=wrap_content
    layout_gravity=center
    layout_marginTop=316dp
    android:textSize=12sp
    android:textColor=@color/text_hint
    android:gravity=center
    android:paddingStart=32dp, android:paddingEnd=32dp
    android:text=@string/empty_drawer_body
    android:visibility=gone

    네 번째 자식 — ProgressBar id=pbLoading:
    layout_width=wrap_content, layout_height=wrap_content
    layout_gravity=center
    layout_marginTop=273dp
    android:indeterminate=true
    android:visibility=gone
  </action>
  <verify>
    <automated>cd c:\Users\KTDS\git\mindJellyAndroid && gradlew.bat compileDebugSources 2>&1 | tail -5</automated>
  </verify>
  <acceptance_criteria>
    - activity_jelly_drawer.xml 루트 태그가 FrameLayout이다
    - activity_jelly_drawer.xml에 id=tvEmptyState, id=tvEmptyStateBody, id=pbLoading가 모두 존재한다
    - activity_jelly_drawer.xml의 rvJellyList에 layout_marginTop="273dp"가 유지된다
    - activity_jelly_drawer.xml에 android:background="@drawable/basic_background"가 루트에 있다
    - ./gradlew compileDebugSources가 BUILD SUCCESSFUL로 종료된다
  </acceptance_criteria>
  <done>activity_jelly_drawer.xml FrameLayout 재구성 완료 — rvJellyList, tvEmptyState, tvEmptyStateBody, pbLoading 4개 뷰 구비</done>
</task>

<task type="auto" tdd="true">
  <name>Task D-2: JellyDrawerActivity — ViewBinding 전환 + 빈 상태 + isLoading + startAging 콜백 + 한국어 에러</name>
  <files>
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java
  </files>
  <read_first>
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java (현재 전체 — rvJellyList 선언, SessionManager 체크, getJellyList observe 패턴)
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/viewmodel/JellyViewModel.java (Plan A 수정 후 — isLoading 필드, startAging 메서드 시그니처 확인)
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerAdapter.java (Plan C 수정 후 — OnStartAgingListener 인터페이스, submitList 메서드 확인)
    - app/src/main/java/com/mindJellyProject/mindjelly/common/ErrorMessageUtil.java (Plan A 신규 — getKoreanMessage 시그니처 확인)
    - app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmo/view/AgingRoomActivity.java (패키지 경로 확인)
    - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java (Plan B 수정 후 — ViewBinding 사용 패턴 참조)
    - .planning/phases/02-핵심루프/02-UI-SPEC.md (Component Contract 3 visibility state machine)
  </read_first>
  <behavior>
    - ActivityJellyDrawerBinding을 사용한다 (binding = ActivityJellyDrawerBinding.inflate(getLayoutInflater()))
    - getJellyList 호출 전 jellyViewModel.isLoading.postValue(true)가 호출된다
    - isLoading observe: true → pbLoading VISIBLE, false → pbLoading GONE
    - getJellyList 성공 + 빈 리스트: rvJellyList GONE, tvEmptyState VISIBLE, tvEmptyStateBody VISIBLE
    - getJellyList 성공 + 비어있지 않은 리스트: rvJellyList VISIBLE, tvEmptyState GONE, tvEmptyStateBody GONE, adapter.submitList(list)
    - getJellyList 에러: ErrorMessageUtil.getKoreanMessage(resource, this) Toast
    - startAging 성공: success_aging_started Toast + AgingRoomActivity로 이동
    - startAging 에러: ErrorMessageUtil.getKoreanMessage(resource, this) Toast
    - userId == -1L: 기존 LoginActivity 리다이렉트 로직 유지
  </behavior>
  <action>
    JellyDrawerActivity.java를 다음과 같이 수정한다.

    1. ViewBinding 추가:
       클래스 필드: private ActivityJellyDrawerBinding binding;
       onCreate 첫 줄들을 교체:
         기존: setContentView(R.layout.activity_jelly_drawer)
         변경: binding = ActivityJellyDrawerBinding.inflate(getLayoutInflater()); setContentView(binding.getRoot());
       WindowInsets: findViewById(R.id.main) → binding.getRoot() 또는 binding.main
       RecyclerView 필드 초기화 제거: rvJellyList = findViewById(R.id.rvJellyList) 삭제
       이후 rvJellyList 참조를 모두 binding.rvJellyList로 교체

    2. isLoading observe 연결 (QUAL-01):
       onCreate에서 jellyViewModel 초기화 직후 추가:
       jellyViewModel.isLoading.observe(this, loading -> {
           binding.pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
       });

    3. getJellyList 호출 직전 isLoading 활성화:
       기존 jellyViewModel.getJellyList(userId).observe(...) 호출 앞에:
       jellyViewModel.isLoading.postValue(true);

    4. getJellyList observe 블록 재작성 (빈 상태 + 에러 한국어 + submitList):
       jellyViewModel.getJellyList(userId).observe(this, resource -> {
           jellyViewModel.isLoading.postValue(false);
           if (resource != null && resource.isSuccess()) {
               List<JellyDrawerResDTO> list = resource.getData();
               if (list == null || list.isEmpty()) {
                   binding.rvJellyList.setVisibility(View.GONE);
                   binding.tvEmptyState.setVisibility(View.VISIBLE);
                   binding.tvEmptyStateBody.setVisibility(View.VISIBLE);
               } else {
                   binding.rvJellyList.setVisibility(View.VISIBLE);
                   binding.tvEmptyState.setVisibility(View.GONE);
                   binding.tvEmptyStateBody.setVisibility(View.GONE);
                   adapter.submitList(list);
               }
           } else if (resource != null && resource.isError()) {
               binding.rvJellyList.setVisibility(View.GONE);
               binding.tvEmptyState.setVisibility(View.GONE);
               binding.tvEmptyStateBody.setVisibility(View.GONE);
               Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
           }
       });

    5. startAging 콜백 연결 (DRAW-03) — adapter 초기화 직후 추가:
       adapter.setOnStartAgingListener(jellyId -> {
           jellyViewModel.startAging(jellyId).observe(this, resource -> {
               jellyViewModel.isLoading.postValue(false);
               if (resource != null && resource.isSuccess()) {
                   Toast.makeText(this, getString(R.string.success_aging_started), Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(JellyDrawerActivity.this, AgingRoomActivity.class));
               } else if (resource != null && resource.isError()) {
                   Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
               }
           });
       });

    6. import 추가 확인:
       - com.mindJellyProject.mindjelly.databinding.ActivityJellyDrawerBinding
       - com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view.AgingRoomActivity
       - com.mindJellyProject.mindjelly.common.ErrorMessageUtil
       - com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO
       - java.util.List

    7. 기존 제거 대상:
       - private RecyclerView rvJellyList 필드 선언 제거
       - rvJellyList = findViewById(...) 제거
       - adapter.setJellyList() 호출 제거 (존재하면)
       - 기존 영문 에러 Toast("젤리 리스트를 불러오는데 실패했습니다: " + errorMessage) 제거
  </action>
  <verify>
    <automated>cd c:\Users\KTDS\git\mindJellyAndroid && gradlew.bat compileDebugSources 2>&1 | tail -5</automated>
  </verify>
  <acceptance_criteria>
    - JellyDrawerActivity.java에 "ActivityJellyDrawerBinding" 참조가 존재한다
    - JellyDrawerActivity.java에 "isLoading.observe" 호출이 존재한다
    - JellyDrawerActivity.java에 "isLoading.postValue(true)" 호출이 존재한다
    - JellyDrawerActivity.java에 "tvEmptyState" 참조(setVisibility 또는 binding.tvEmptyState)가 존재한다
    - JellyDrawerActivity.java에 "submitList" 호출이 존재한다
    - JellyDrawerActivity.java에 "setOnStartAgingListener" 호출이 존재한다
    - JellyDrawerActivity.java에 "AgingRoomActivity" 이동 코드(startActivity)가 존재한다
    - JellyDrawerActivity.java에 "ErrorMessageUtil.getKoreanMessage" 호출이 존재한다
    - JellyDrawerActivity.java에 "젤리 리스트를 불러오는데 실패했습니다" 하드코딩 문자열이 없다
    - ./gradlew compileDebugSources가 BUILD SUCCESSFUL로 종료된다
  </acceptance_criteria>
  <done>JellyDrawerActivity ViewBinding 전환 완료, 빈 상태 UI 연결(DRAW-04), isLoading 로딩 인디케이터(QUAL-01), startAging → AgingRoom(DRAW-03), 한국어 에러 Toast(QUAL-02)</done>
</task>

</tasks>

<threat_model>
## Trust Boundaries

| Boundary | Description |
|----------|-------------|
| startAging 콜백 | adapter에서 전달된 jellyId가 해당 사용자 소유인지 서버가 검증해야 함 |
| getJellyList 응답 | 서버가 다른 사용자의 데이터를 반환하지 않도록 JWT 기반 서버 측 필터링 필요 |

## STRIDE Threat Register

| Threat ID | Category | Component | Disposition | Mitigation Plan |
|-----------|----------|-----------|-------------|-----------------|
| T-02D-01 | Spoofing | userId == -1L 체크 | mitigate | SessionManager.getUserId() == -1L 시 LoginActivity 리다이렉트 (기존 로직 유지) |
| T-02D-02 | Tampering | startAging jellyId | accept | 클라이언트는 Adapter에서 받은 jellyId를 그대로 전달 — 서버가 해당 jelly의 소유권과 현재 status를 검증해야 함 |
| T-02D-03 | Denial of Service | isLoading 미해제 | mitigate | getJellyList/startAging observe의 성공·에러 양 분기 모두에서 isLoading.postValue(false) 명시 호출 |
</threat_model>

<verification>
Wave 3 전체 통과 기준 (Phase 2 gate):
- ./gradlew compileDebugSources → BUILD SUCCESSFUL
- ./gradlew testDebugUnitTest → BUILD SUCCESSFUL
- activity_jelly_drawer.xml FrameLayout 루트: grep -c "FrameLayout" activity_jelly_drawer.xml (1 이상)
- JellyDrawerActivity ViewBinding: grep ActivityJellyDrawerBinding JellyDrawerActivity.java
- JellyDrawerActivity 한국어 에러: grep ErrorMessageUtil JellyDrawerActivity.java
- 에뮬레이터 수동 E2E (성공 기준):
  1. 감정 2개 선택 → 합성 프리뷰 이미지 표시 (JELLY-02)
  2. 일기 작성 → 저장 버튼 → 성공 Toast → JellyDrawerActivity 이동 (JELLY-04/05)
  3. JellyDrawerActivity에서 날짜·아이콘 2개·상태 배지 목록 표시 (DRAW-01/02)
  4. 기록 없을 때 빈 상태 안내 표시 (DRAW-04)
</verification>

<success_criteria>
- activity_jelly_drawer.xml: FrameLayout 루트(id=main), rvJellyList(273dp top/53dp bottom 마진 보존), tvEmptyState, tvEmptyStateBody, pbLoading 모두 gone 초기값
- JellyDrawerActivity: ActivityJellyDrawerBinding 사용, binding.rvJellyList/tvEmptyState/pbLoading 접근
- isLoading observe: pbLoading VISIBLE/GONE 토글
- getJellyList: 빈 리스트 → tvEmptyState VISIBLE / 비어있지 않은 리스트 → rvJellyList VISIBLE + adapter.submitList()
- startAging 성공: success_aging_started Toast + AgingRoomActivity 이동
- 에러 경로: ErrorMessageUtil.getKoreanMessage() 한국어 Toast (영문 하드코딩 없음)
- 전체 컴파일 통과
</success_criteria>

<output>
완료 후 .planning/phases/02-핵심루프/02-D-SUMMARY.md 생성
</output>
