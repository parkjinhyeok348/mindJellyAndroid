---
phase: 02-핵심루프
plan: B
type: execute
wave: 2
depends_on: [02-A]
files_modified:
  - app/src/main/res/layout/item_basic_emo.xml
  - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/BasicEmoAdapter.java
  - app/src/main/res/layout/activity_today_jelly.xml
  - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/viewmodel/JellyCombViewModel.java
autonomous: true
requirements:
  - JELLY-01
  - JELLY-02
  - JELLY-03
  - JELLY-04
  - JELLY-05

must_haves:
  truths:
    - "감정 그리드에 아이콘과 함께 감정 이름(tvEmoName)이 표시된다"
    - "감정 2개 선택 시 합성 젤리 프리뷰가 즉시 업데이트된다"
    - "etDiary EditText에 텍스트를 입력할 수 있다"
    - "btnSave 클릭 시 JellySaveReqDTO가 조립되어 createJelly API가 호출된다"
    - "저장 성공 후 JellyDrawerActivity로 이동한다"
    - "모든 네트워크 요청 중 pbLoading이 표시된다"
  artifacts:
    - path: "app/src/main/res/layout/item_basic_emo.xml"
      provides: "감정 그리드 아이템 — 아이콘 + 이름 레이블"
      contains: "tvEmoName"
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/BasicEmoAdapter.java"
      provides: "BasicEmoAdapter — tvEmoName 바인딩"
    - path: "app/src/main/res/layout/activity_today_jelly.xml"
      provides: "TodayJelly 레이아웃 — etDiary + btnSave + pbLoading 추가"
      contains: "etDiary, btnSave, pbLoading"
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java"
      provides: "TodayJellyActivity — 저장 버튼 로직, jellyCombId 캐싱, 화면 이동"
  key_links:
    - from: "BasicEmoAdapter.OnSelectionChangedListener"
      to: "TodayJellyActivity.fetchCombinedJellyIcon() + jellyCombId fetch"
      via: "setOnSelectionChangedListener 콜백"
    - from: "TodayJellyActivity.btnSave.setOnClickListener"
      to: "jellyViewModel.createJelly(JellySaveReqDTO)"
      via: "observe → Resource.isSuccess() → startActivity(JellyDrawerActivity)"
    - from: "jellyViewModel.isLoading"
      to: "pbLoading.setVisibility()"
      via: "observe → VISIBLE/GONE"
---

<objective>
TodayJellyActivity의 전체 사용자 플로우를 완성한다: 감정 이름 표시 → 2개 선택 → 프리뷰 → 일기 작성 → 저장 → 젤리서랍 이동.

Purpose: 앱의 핵심 입력 경로(감정 선택 → 젤리 저장)를 사용자가 처음부터 끝까지 실행할 수 있게 한다.
Output: item_basic_emo.xml 재구성, BasicEmoAdapter tvEmoName 바인딩, activity_today_jelly.xml에 diary/save/loading 추가, TodayJellyActivity 저장 로직 완성
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
@.planning/phases/02-핵심루프/02-A-SUMMARY.md

<interfaces>
<!-- Plan A에서 확립된 계약 + 현재 코드베이스 상태 -->

From BasicEmoResDTO (기존):
  getEmoId() → Long
  getEmoName() → String   ← 이미 존재 (UI-SPEC에서 확인, RESEARCH.md JELLY-01)
  getEmoIcon() → String (상대경로, e.g. "/images/joy.png")

From BasicEmoAdapter.java (현재):
  class BasicEmoAdapter extends RecyclerView.Adapter&lt;BasicEmoAdapter.ViewHolder&gt;
  private final String serverUrl = "http://10.0.2.2:8080"
  ViewHolder: ImageView emoImageView  ← tvEmoName 없음, 추가 필요
  setEmos(List&lt;BasicEmoResDTO&gt;) — selectedPositions.clear() + notifyDataSetChanged()
  getSelectedEmos() → List&lt;BasicEmoResDTO&gt;
  interface OnSelectionChangedListener { void onSelectionChanged(List&lt;BasicEmoResDTO&gt; selectedEmos) }
  setOnSelectionChangedListener(OnSelectionChangedListener)
  선택 로직: selectedPositions(max 2), setSelected(true/false)

From item_basic_emo.xml (현재):
  FrameLayout root — background=@drawable/sel_jelly_item, padding=4dp
    ImageView id=emoImageView — 60dp×60dp, layout_gravity=center, layout_margin=10dp
  → 루트를 vertical LinearLayout으로 변환, FrameLayout을 자식으로, tvEmoName 추가

From TodayJellyActivity.java (현재):
  binding: ActivityTodayJellyBinding
  viewModel: BasicEmoViewModel — getAllBasicEmoList() observe
  jellyCombViewModel: JellyCombViewModel — getJellyIcon(id1, id2, false) observe
  serverUrl = "http://10.0.2.2:8080"
  fetchCombinedJellyIcon(Long firstEmo, Long secondEmo) — observe → Glide.load(serverUrl + iconPath)
  binding.combinedJellyImageView — 2개 미만 선택 시 GONE
  → 추가 필요: JellyViewModel, etDiary, btnSave, pbLoading, jellyCombId 캐싱

From Plan A (JellyCombService — 신규 추가됨):
  getJellyCombId(Long firstEmo, Long secondEmo) → Call&lt;Long&gt;
  → JellyCombRepository에도 getJellyCombId 메서드 추가 필요

From Plan A (JellyViewModel — 수정됨):
  public final MutableLiveData&lt;Boolean&gt; isLoading
  createJelly(JellySaveReqDTO reqDTO) → LiveData&lt;Resource&lt;Jelly&gt;&gt;
  startAging(Long jellyId) → LiveData&lt;Resource&lt;ResponseBody&gt;&gt;

From JellySaveReqDTO (현재):
  constructor: JellySaveReqDTO(Long userId, Long jellyCombId, String jellyName, String content,
                               String agingPeriod, String createDate, List&lt;JellyImage&gt; jellyImages)
  agingPeriod: String "7" (A4 가정)
  jellyImages: null 허용 (A3 가정)

From SessionManager (기존):
  SessionManager.getInstance(Context).getUserId() → long (sentinel: -1L)

From ErrorMessageUtil (Plan A 신규):
  getKoreanMessage(Resource&lt;?&gt; resource, Context context) → String

From activity_today_jelly.xml (현재):
  ConstraintLayout id=main, background=@drawable/basic_background
  ImageView id=combinedJellyImageView — 100dp, constraintBottom_toTopOf=emoRecyclerView, marginBottom=30dp
  RecyclerView id=emoRecyclerView — 0dp×0dp, height_percent=0.7, marginBottom=20dp, spanCount=3
  → 수정: combinedJellyImageView marginBottom=32dp, height_percent=0.5
  → 추가: etDiary, btnSave, pbLoading (UI-SPEC.md activity_today_jelly.xml 섹션 정확히 따름)
</interfaces>
</context>

<tasks>

<task type="auto" tdd="true">
  <name>Task B-1: item_basic_emo.xml 재구성 + BasicEmoAdapter tvEmoName 바인딩</name>
  <files>
    app/src/main/res/layout/item_basic_emo.xml
    app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/BasicEmoAdapter.java
  </files>
  <read_first>
    - app/src/main/res/layout/item_basic_emo.xml (현재 FrameLayout 루트 구조 확인)
    - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/BasicEmoAdapter.java (ViewHolder 필드, onBindViewHolder 전체)
    - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/model/BasicEmoResDTO.java (getEmoName() 존재 여부 확인)
    - .planning/phases/02-핵심루프/02-UI-SPEC.md (Component Contract 1: item_basic_emo.xml 섹션)
  </read_first>
  <behavior>
    - BasicEmoAdapter ViewHolder에 tvEmoName 필드가 존재한다
    - onBindViewHolder에서 holder.tvEmoName.setText(emo.getEmoName())가 호출된다
    - 선택 상태(setSelected)는 기존과 동일하게 FrameLayout에 적용된다
  </behavior>
  <action>
    item_basic_emo.xml을 UI-SPEC.md Component Contract 1에 따라 재작성한다:
    - 루트를 vertical LinearLayout으로 변경 (wrap_content × wrap_content, orientation=vertical)
    - 기존 FrameLayout(배경=@drawable/sel_jelly_item, padding=4dp)을 LinearLayout의 첫 번째 자식으로 유지
    - FrameLayout 내부 emoImageView (60dp×60dp, layout_gravity=center, layout_margin=10dp) 유지
    - FrameLayout 아래에 TextView id=tvEmoName 추가:
      layout_width=match_parent, layout_height=wrap_content, gravity=center, textSize=12sp,
      textColor=@color/text_secondary, layout_marginTop=4dp, maxLines=1, ellipsize=end

    BasicEmoAdapter.java를 다음과 같이 수정한다:
    - ViewHolder에 TextView tvEmoName 필드 추가
    - ViewHolder 생성자에서 tvEmoName = itemView.findViewById(R.id.tvEmoName) 추가
    - onBindViewHolder에서 holder.tvEmoName.setText(emo.getEmoName()) 추가
    - 기존 Glide 이미지 로딩, 선택 로직, selectedPositions, OnSelectionChangedListener는 변경하지 않는다
    - 주의: FrameLayout이 LinearLayout의 자식이 되면서 선택 상태(setSelected)의 대상을 확인한다.
      현재 holder.itemView.setSelected(...)는 루트 뷰(LinearLayout)에 적용되지만,
      sel_jelly_item 셀렉터는 FrameLayout의 background에 설정되어 있으므로
      holder.itemView.setSelected 대신 FrameLayout에 직접 setSelected를 적용하거나
      FrameLayout에 대한 별도 참조를 ViewHolder에 추가해야 한다.
      ViewHolder에 frameLayout 필드를 추가하고 frameLayout.setSelected(...)를 사용한다.
  </action>
  <verify>
    <automated>cd c:\Users\KTDS\git\mindJellyAndroid && gradlew.bat compileDebugSources 2>&1 | tail -5</automated>
  </verify>
  <acceptance_criteria>
    - item_basic_emo.xml에 id=tvEmoName인 TextView가 존재한다
    - item_basic_emo.xml 루트 태그가 LinearLayout이다
    - BasicEmoAdapter.ViewHolder에 tvEmoName 필드 선언이 존재한다
    - BasicEmoAdapter.onBindViewHolder에 tvEmoName.setText 호출이 존재한다
    - ./gradlew compileDebugSources가 BUILD SUCCESSFUL로 종료된다
  </acceptance_criteria>
  <done>item_basic_emo.xml 재구성 완료, BasicEmoAdapter tvEmoName 바인딩 완료, 감정 이름이 그리드에 표시됨(JELLY-01 충족)</done>
</task>

<task type="auto" tdd="true">
  <name>Task B-2: activity_today_jelly.xml 확장 + TodayJellyActivity 저장 로직 완성</name>
  <files>
    app/src/main/res/layout/activity_today_jelly.xml
    app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombRepository.java
  </files>
  <read_first>
    - app/src/main/res/layout/activity_today_jelly.xml (현재 combinedJellyImageView + emoRecyclerView만 존재)
    - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java (ViewBinding, jellyCombViewModel 사용 패턴 전체)
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombRepository.java (getJellyIcon 구현 패턴)
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellySaveReqDTO.java (생성자 시그니처 확인)
    - app/src/main/java/com/mindJellyProject/mindjelly/common/SessionManager.java (getUserId() 반환 타입 확인)
    - .planning/phases/02-핵심루프/02-CONTEXT.md (D-04, D-05, D-06 프리뷰 결정)
    - .planning/phases/02-핵심루프/02-UI-SPEC.md (Component Contract 2: activity_today_jelly.xml 섹션)
    - .planning/phases/02-핵심루프/02-RESEARCH.md (Pitfall 1 jellyCombId, Pitfall 2 LiveData 누적)
  </read_first>
  <behavior>
    - etDiary가 비어있을 때 btnSave 클릭 시 getString(R.string.error_diary_empty) Toast를 표시하고 API를 호출하지 않는다
    - jellyCombId가 캐싱되지 않은 상태(null)에서 btnSave 클릭 시 getString(R.string.error_generic) Toast를 표시한다
    - createJelly 성공(isSuccess() == true) 시 JellyDrawerActivity로 이동하고 finish()를 호출한다
    - isLoading true → pbLoading VISIBLE, btnSave clickable=false; false → GONE, clickable=true
  </behavior>
  <action>
    activity_today_jelly.xml을 UI-SPEC.md Component Contract 2에 따라 수정한다:
    - combinedJellyImageView: layout_marginBottom을 30dp → 32dp로 변경, contentDescription=@string/cd_jelly_preview
    - emoRecyclerView: constraint_height_percent를 0.7 → 0.5로 변경, layout_marginBottom=16dp (기존 20dp에서 변경), constraintBottom_toTopOf를 @id/etDiary로 변경
    - EditText id=etDiary 추가: width=0dp, height=wrap_content, minHeight=80dp, layout_marginStart=16dp, layout_marginEnd=16dp, layout_marginBottom=12dp, background=@drawable/bg_edit_text, padding=12dp, inputType=textMultiLine|textCapSentences, gravity=top|start, textSize=14sp, textColor=@color/text_primary, hint=@string/hint_diary_entry, textColorHint=@color/text_hint, maxLines=5. 제약: constraintBottom_toTopOf=@id/btnSave, start/end=parent
    - Button id=btnSave 추가: width=0dp, height=48dp, layout_marginStart=16dp, layout_marginEnd=16dp, layout_marginBottom=48dp, text=@string/btn_save_jelly, textSize=14sp, backgroundTint=@color/accent_blue, textColor=@color/white. 제약: constraintBottom_toBottomOf=parent, start/end=parent
    - ProgressBar id=pbLoading 추가: width=wrap_content, height=wrap_content, visibility=gone, indeterminate=true. 제약: center_horizontal=parent, center_vertical=parent

    JellyCombRepository.java에 getJellyCombId 메서드를 추가한다:
    - getJellyIcon과 동일한 enqueue/postValue 패턴 사용
    - public LiveData&lt;Resource&lt;Long&gt;&gt; getJellyCombId(Long firstEmo, Long secondEmo)
    - jellyCombService.getJellyCombId(firstEmo, secondEmo).enqueue(...)
    - 성공 시 Resource.success(response.body()), 실패 시 Resource.error(...)

    TodayJellyActivity.java를 다음과 같이 수정한다:

    1. 필드 추가:
       - private JellyViewModel jellyViewModel
       - private Long cachedJellyCombId = null (Pitfall 1 해결 — jellyCombId 캐싱)
       - import java.text.SimpleDateFormat, java.util.Date, java.util.Locale

    2. onCreate에서 jellyViewModel 초기화:
       jellyViewModel = new ViewModelProvider(this).get(JellyViewModel.class)

    3. onCreate에서 isLoading observe 연결:
       jellyViewModel.isLoading.observe(this, loading -> {
           binding.pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
           binding.btnSave.setClickable(!loading);
       })

    4. setupRecyclerView의 OnSelectionChangedListener 수정:
       selectedEmos.size() == 2 인 경우 기존 fetchCombinedJellyIcon(id1, id2) 호출에 더해
       jellyCombViewModel.getJellyCombId(id1, id2)를 observe하여 cachedJellyCombId에 캐싱:
       jellyCombViewModel.getJellyCombId(id1, id2)가 JellyCombViewModel에 없으면
       직접 repository를 통해 호출하거나 JellyCombViewModel에 메서드를 추가한다.
       (JellyCombViewModel에 public LiveData&lt;Resource&lt;Long&gt;&gt; getJellyCombId(Long, Long) 추가 필요)
       observe 성공 시: if(resource.isSuccess()) cachedJellyCombId = resource.getData()
       2개 미만 선택 해제 시: cachedJellyCombId = null

    5. onCreate에서 btnSave 클릭 리스너 설정 (Pitfall 2 방지 — observe 중복 등록 없이 버튼 클릭 내부에서 관찰):
       binding.btnSave.setOnClickListener(v -> {
           String diary = binding.etDiary.getText().toString().trim();
           if (diary.isEmpty()) {
               Toast.makeText(this, getString(R.string.error_diary_empty), Toast.LENGTH_SHORT).show();
               return;
           }
           if (cachedJellyCombId == null) {
               Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
               return;
           }
           long userId = SessionManager.getInstance(this).getUserId();
           if (userId == -1L) {
               // 인증 만료 — LoginActivity로 이동 (기존 JellyDrawerActivity 패턴 동일)
               return;
           }
           String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
           JellySaveReqDTO reqDTO = new JellySaveReqDTO(userId, cachedJellyCombId,
               "오늘의 젤리", diary, "7", today, null);
           jellyViewModel.createJelly(reqDTO).observe(this, resource -> {
               if (resource.isSuccess()) {
                   Toast.makeText(this, getString(R.string.success_jelly_saved), Toast.LENGTH_SHORT).show();
                   jellyViewModel.isLoading.postValue(false);
                   startActivity(new Intent(this, JellyDrawerActivity.class));
                   finish();
               } else if (resource.isError()) {
                   jellyViewModel.isLoading.postValue(false);
                   Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
               }
           });
       })

    주의사항:
    - fetchCombinedJellyIcon()의 기존 observe 패턴(매번 새 LiveData observe)은 Pitfall 2에서 언급됨.
      현재는 매번 새 MutableLiveData가 반환되므로 기술적으로 다른 LiveData이지만,
      최소한 combinedJellyImageView observe는 기존 방식 유지 (MVP 품질 허용).
    - JellyCombViewModel에 getJellyCombId 메서드가 없으면 이 태스크에서 추가한다.
  </action>
  <verify>
    <automated>cd c:\Users\KTDS\git\mindJellyAndroid && gradlew.bat compileDebugSources 2>&1 | tail -5</automated>
  </verify>
  <acceptance_criteria>
    - activity_today_jelly.xml에 id=etDiary, id=btnSave, id=pbLoading이 모두 존재한다
    - activity_today_jelly.xml의 emoRecyclerView constraint_height_percent 값이 0.5이다
    - TodayJellyActivity.java에 "cachedJellyCombId" 필드 선언이 존재한다
    - TodayJellyActivity.java에 "JellyDrawerActivity" 이동 코드(startActivity)가 존재한다
    - TodayJellyActivity.java에 "error_diary_empty" 문자열 참조가 존재한다
    - TodayJellyActivity.java에 "isLoading.observe" 호출이 존재한다
    - ./gradlew compileDebugSources가 BUILD SUCCESSFUL로 종료된다
  </acceptance_criteria>
  <done>activity_today_jelly.xml에 etDiary/btnSave/pbLoading 추가 완료, TodayJellyActivity 저장 로직 완성(JELLY-03/04/05), isLoading observe 연결(QUAL-01 부분), 컴파일 통과</done>
</task>

</tasks>

<threat_model>
## Trust Boundaries

| Boundary | Description |
|----------|-------------|
| EditText → JellySaveReqDTO.content | 사용자 입력 텍스트가 API 바디로 전달되는 경계 |
| cachedJellyCombId → JellySaveReqDTO | null 미검증 시 API 400 에러 |

## STRIDE Threat Register

| Threat ID | Category | Component | Disposition | Mitigation Plan |
|-----------|----------|-----------|-------------|-----------------|
| T-02B-01 | Tampering | etDiary → content 필드 | mitigate | btnSave onClick에서 trim().isEmpty() 체크 후 거부(per RESEARCH.md Security Domain V5, error_diary_empty Toast) |
| T-02B-02 | Tampering | cachedJellyCombId null | mitigate | cachedJellyCombId == null 체크 → error_generic Toast, API 미호출 |
| T-02B-03 | Spoofing | userId == -1L | mitigate | SessionManager.getUserId() == -1L 체크 → LoginActivity 리다이렉트(기존 패턴 동일) |
| T-02B-04 | Denial of Service | isLoading 미해제 | accept | createJelly 성공/실패 양 분기에서 isLoading.postValue(false) 명시적 호출 |
</threat_model>

<verification>
Wave 2-B 완료 기준:
- ./gradlew compileDebugSources → BUILD SUCCESSFUL
- ./gradlew testDebugUnitTest → BUILD SUCCESSFUL
- item_basic_emo.xml에 tvEmoName 존재: grep tvEmoName app/src/main/res/layout/item_basic_emo.xml
- activity_today_jelly.xml에 etDiary 존재: grep etDiary app/src/main/res/layout/activity_today_jelly.xml
- TodayJellyActivity에 JellyDrawerActivity startActivity 존재: grep JellyDrawerActivity TodayJellyActivity.java
</verification>

<success_criteria>
- item_basic_emo.xml: vertical LinearLayout 루트, FrameLayout(sel_jelly_item 배경) 자식, tvEmoName(12sp, text_secondary) 존재
- BasicEmoAdapter: tvEmoName 필드 + setText(getEmoName()) 호출 존재
- activity_today_jelly.xml: etDiary(minHeight=80dp, bg_edit_text), btnSave(48dp, accent_blue), pbLoading(center, gone) 존재
- TodayJellyActivity: cachedJellyCombId 캐싱, 빈 일기 거부, jellyCombId null 거부, createJelly observe, JellyDrawerActivity 이동
- isLoading.observe: pbLoading VISIBLE/GONE + btnSave clickable 토글
- ErrorMessageUtil.getKoreanMessage 에러 Toast 사용
</success_criteria>

<output>
완료 후 .planning/phases/02-핵심루프/02-B-SUMMARY.md 생성
</output>
