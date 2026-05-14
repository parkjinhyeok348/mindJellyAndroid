---
phase: 02-핵심루프
plan: C
type: execute
wave: 2
depends_on: [02-A]
files_modified:
  - app/src/main/res/layout/item_jelly_drawer.xml
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerAdapter.java
autonomous: true
requirements:
  - DRAW-01
  - DRAW-02
  - DRAW-03

must_haves:
  truths:
    - "item_jelly_drawer.xml에 emo1ImageView, emo2ImageView, btnStartAging이 존재한다"
    - "JellyDrawerAdapter가 ListAdapter<JellyDrawerResDTO, ViewHolder>를 extends한다"
    - "onBindViewHolder에서 emo1Icon/emo2Icon URL을 Glide로 로딩한다"
    - "status 값에 따라 tvAgingStatus 배지의 배경색·텍스트색이 3가지로 구분된다"
    - "status=='WAITING'일 때만 btnStartAging이 VISIBLE이고, 나머지는 GONE이다"
    - "OnStartAgingListener 인터페이스와 setOnStartAgingListener()가 존재한다"
  artifacts:
    - path: "app/src/main/res/layout/item_jelly_drawer.xml"
      provides: "젤리서랍 아이템 레이아웃 — 감정 아이콘 2개 + 날짜 + 상태 배지 + 숙성 버튼"
      contains: "emo1ImageView, emo2ImageView, tvCreateDate, tvAgingStatus, btnStartAging"
    - path: "app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerAdapter.java"
      provides: "JellyDrawerAdapter — ListAdapter 마이그레이션 + 3-state 배지 + 숙성 버튼"
  key_links:
    - from: "JellyDrawerAdapter.btnStartAging.setOnClickListener"
      to: "OnStartAgingListener.onStartAging(jellyId)"
      via: "listener 인터페이스 콜백"
    - from: "JellyDrawerActivity (Plan D)"
      to: "adapter.setOnStartAgingListener(jellyId -> ...)"
      via: "setOnStartAgingListener 세터"
    - from: "JellyDrawerAdapter.onBindViewHolder"
      to: "Glide.load(serverUrl + emo1Icon)"
      via: "BasicEmoAdapter와 동일한 serverUrl 패턴"
---

<objective>
JellyDrawerAdapter를 ListAdapter로 마이그레이션하고 감정 아이콘 2개, 3단계 숙성 배지, 숙성시키기 버튼을 구현한다.

Purpose: DRAW-01(날짜+아이콘 표시), DRAW-02(숙성 상태 배지), DRAW-03(숙성시키기 버튼 인터페이스)를 Adapter 계층에서 완성한다. JellyDrawerActivity(Plan D)는 이 Adapter의 OnStartAgingListener를 구현하여 API 호출을 담당한다.
Output: item_jelly_drawer.xml 재구성, JellyDrawerAdapter ListAdapter 마이그레이션 + 3-state 배지 + 버튼 콜백 인터페이스
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
<!-- Plan A에서 확립된 계약 + 현재 코드베이스 상태 -->

From JellyDrawerResDTO.java (Plan A 수정 후):
  getJellyId() → Long
  getJellyCombId() → Long
  getAging() → Boolean (기존 유지)
  getCreateDate() → String
  getEmo1Name() → String   ← Plan A에서 추가됨
  getEmo1Icon() → String   ← 상대경로 (e.g. "/images/joy.png"), serverUrl 앞에 붙여야 함
  getEmo2Name() → String   ← Plan A에서 추가됨
  getEmo2Icon() → String   ← 상대경로
  getStatus() → String     ← "WAITING" / "AGING" / "MATURED" / null

From JellyDrawerAdapter.java (현재):
  extends RecyclerView.Adapter<JellyDrawerAdapter.ViewHolder>
  private List<JellyDrawerResDTO> jellyList = new ArrayList<>()
  setJellyList(List<JellyDrawerResDTO>) — notifyDataSetChanged() 호출
  ViewHolder: TextView tvJellyId, tvCreateDate, tvAgingStatus
  onBindViewHolder: tvJellyId.setText("Jelly ID: " + jellyId), tvAgingStatus.setText(isAging ? "에이징 중" : "완성")
  → 전체 재구성 필요: ListAdapter 마이그레이션, ivJellyIcon/tvJellyId 제거, emo 아이콘 추가, 배지 3-state

From item_jelly_drawer.xml (현재):
  LinearLayout root (horizontal, padding=12dp)
    ImageView id=ivJellyIcon (60dp×60dp)      ← 제거
    LinearLayout (vertical, weight=1)
      TextView id=tvJellyId (16sp bold)        ← 제거
      TextView id=tvCreateDate (14sp)          ← 유지, 16sp bold로 변경 (UI-SPEC Primary role)
    TextView id=tvAgingStatus                  ← 유지, 배지 스타일 적용

From Plan A resources:
  @color/badge_waiting_bg(#FFFFEEBA), @color/badge_waiting_text(#FF856404)
  @color/badge_aging_bg(#FFD1ECF1), @color/badge_aging_text(#FF0C5460)
  @color/badge_complete_bg(#FFD4EDDA), @color/badge_complete_text(#FF155724)
  @color/accent_blue(#FF0000FF), @color/text_primary(#FF333333)
  @string/status_waiting, @string/status_aging, @string/status_complete
  @string/btn_start_aging, @string/cd_emo1_icon, @string/cd_emo2_icon
  @drawable/bg_status_badge (radius=4dp, 기본색 #FFFFEEBA)

From BasicEmoAdapter.java (기존 참조 패턴):
  private final String serverUrl = "http://10.0.2.2:8080"
  Glide.with(holder.itemView.getContext()).load(serverUrl + iconPath)
      .placeholder(android.R.drawable.ic_menu_report_image)
      .error(android.R.drawable.stat_notify_error)
      .into(imageView)
</interfaces>
</context>

<tasks>

<task type="auto" tdd="true">
  <name>Task C-1: item_jelly_drawer.xml 재구성 — 감정 아이콘 2개 + 날짜 + 배지 + 숙성 버튼</name>
  <files>
    app/src/main/res/layout/item_jelly_drawer.xml
  </files>
  <read_first>
    - app/src/main/res/layout/item_jelly_drawer.xml (현재 구조 전체 — ivJellyIcon/tvJellyId/tvCreateDate/tvAgingStatus 위치 확인)
    - .planning/phases/02-핵심루프/02-UI-SPEC.md (Component Contract 4: item_jelly_drawer.xml 섹션 전체)
    - app/src/main/res/values/colors.xml (Plan A 추가 후 badge_*, accent_blue 존재 확인)
    - app/src/main/res/values/strings.xml (btn_start_aging, cd_emo1_icon, cd_emo2_icon 존재 확인)
    - app/src/main/res/drawable/bg_status_badge.xml (Plan A에서 생성됨, radius=4dp 확인)
  </read_first>
  <behavior>
    - item_jelly_drawer.xml 루트는 horizontal LinearLayout (match_parent × wrap_content, padding=12dp)이다
    - id=emo1ImageView (40dp×40dp)와 id=emo2ImageView (40dp×40dp, marginStart=4dp)가 좌측에 나란히 있다
    - id=tvCreateDate (16sp bold, text_primary)가 중앙 영역에 weight=1로 확장된다
    - id=tvAgingStatus (12sp, paddingH=8dp, paddingV=4dp, bg=@drawable/bg_status_badge)가 우측에 있다
    - id=btnStartAging (visibility=gone, text=@string/btn_start_aging, accent_blue)가 tvAgingStatus 아래에 있다
    - ivJellyIcon과 tvJellyId는 존재하지 않는다
  </behavior>
  <action>
    item_jelly_drawer.xml을 UI-SPEC.md Component Contract 4에 따라 완전히 재작성한다.

    루트: LinearLayout, orientation=horizontal, layout_width=match_parent, layout_height=wrap_content, padding=12dp

    첫 번째 자식 — 감정 아이콘 쌍 (horizontal LinearLayout):
    layout_width=wrap_content, layout_height=wrap_content, android:gravity=center_vertical
      emo1ImageView: width=40dp, height=40dp, scaleType=centerCrop, contentDescription=@string/cd_emo1_icon
      emo2ImageView: width=40dp, height=40dp, scaleType=centerCrop, layout_marginStart=4dp, contentDescription=@string/cd_emo2_icon

    두 번째 자식 — 날짜 텍스트 (vertical LinearLayout):
    layout_width=0dp, layout_weight=1, layout_height=wrap_content, layout_marginStart=12dp
      tvCreateDate: width=wrap_content, height=wrap_content, textSize=16sp, textStyle=bold, textColor=@color/text_primary

    세 번째 자식 — 상태 배지 + 버튼 (vertical LinearLayout):
    layout_width=wrap_content, layout_height=wrap_content, android:gravity=center_vertical
      tvAgingStatus: width=wrap_content, height=wrap_content, textSize=12sp,
        android:paddingHorizontal=8dp, android:paddingVertical=4dp,
        android:background=@drawable/bg_status_badge
      btnStartAging: width=wrap_content, height=wrap_content, layout_marginTop=4dp,
        text=@string/btn_start_aging, textSize=12sp,
        backgroundTint=@color/accent_blue, textColor=@color/white,
        android:visibility=gone, android:minHeight=36dp,
        android:paddingHorizontal=8dp
  </action>
  <verify>
    <automated>cd c:\Users\KTDS\git\mindJellyAndroid && gradlew.bat compileDebugSources 2>&1 | tail -5</automated>
  </verify>
  <acceptance_criteria>
    - item_jelly_drawer.xml에 id=emo1ImageView, id=emo2ImageView가 존재한다
    - item_jelly_drawer.xml에 id=btnStartAging이 존재하고 android:visibility="gone"이다
    - item_jelly_drawer.xml에 id=ivJellyIcon, id=tvJellyId가 존재하지 않는다
    - item_jelly_drawer.xml에 id=tvAgingStatus에 android:background="@drawable/bg_status_badge"가 있다
    - ./gradlew compileDebugSources가 BUILD SUCCESSFUL로 종료된다
  </acceptance_criteria>
  <done>item_jelly_drawer.xml 재구성 완료 — emo1/emo2 ImageView, tvCreateDate(primary), tvAgingStatus(badge), btnStartAging(gone)</done>
</task>

<task type="auto" tdd="true">
  <name>Task C-2: JellyDrawerAdapter — ListAdapter 마이그레이션 + Glide 아이콘 + 3-state 배지 + OnStartAgingListener</name>
  <files>
    app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerAdapter.java
  </files>
  <read_first>
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerAdapter.java (현재 전체 — RecyclerView.Adapter 구조)
    - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyDrawerResDTO.java (Plan A 수정 후 — getStatus(), getEmo1Icon(), getEmo2Icon() 확인)
    - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/BasicEmoAdapter.java (serverUrl + Glide 패턴 참조)
    - .planning/phases/02-핵심루프/02-UI-SPEC.md (Status Badge Contract, Component Contract 4 — GradientDrawable mutate 패턴)
    - app/src/main/res/values/colors.xml (Plan A 추가 후 badge_* 색상 확인)
  </read_first>
  <behavior>
    - JellyDrawerAdapter 클래스가 ListAdapter&lt;JellyDrawerResDTO, JellyDrawerAdapter.ViewHolder&gt;를 extends한다
    - submitList(list) 호출로 리스트가 갱신된다 (setJellyList/notifyDataSetChanged 제거)
    - emo1ImageView에 serverUrl + jelly.getEmo1Icon() URL이 Glide로 로딩된다
    - emo2ImageView에 serverUrl + jelly.getEmo2Icon() URL이 Glide로 로딩된다
    - status=="WAITING": tvAgingStatus 텍스트=@string/status_waiting, 배경=badge_waiting_bg, 글자=badge_waiting_text, btnStartAging VISIBLE
    - status=="AGING": tvAgingStatus 텍스트=@string/status_aging, 배경=badge_aging_bg, 글자=badge_aging_text, btnStartAging GONE
    - status=="MATURED": tvAgingStatus 텍스트=@string/status_complete, 배경=badge_complete_bg, 글자=badge_complete_text, btnStartAging GONE
    - status==null 또는 알 수 없는 값: WAITING 폴백 적용
    - btnStartAging 클릭 시 OnStartAgingListener.onStartAging(jelly.getJellyId())가 호출된다
  </behavior>
  <action>
    JellyDrawerAdapter.java를 다음과 같이 전면 재작성한다.

    클래스 선언:
    public class JellyDrawerAdapter extends ListAdapter&lt;JellyDrawerResDTO, JellyDrawerAdapter.ViewHolder&gt;

    DiffUtil.ItemCallback&lt;JellyDrawerResDTO&gt; 정적 내부 클래스 또는 상수:
    areItemsTheSame: a.getJellyId().equals(b.getJellyId())
    areContentsTheSame: a.getJellyId().equals(b.getJellyId())
      && Objects.equals(a.getStatus(), b.getStatus())
      && Objects.equals(a.getCreateDate(), b.getCreateDate())
      (import java.util.Objects 필요)

    생성자: public JellyDrawerAdapter() { super(DIFF_CALLBACK); }

    OnStartAgingListener 인터페이스:
    public interface OnStartAgingListener { void onStartAging(Long jellyId); }
    private OnStartAgingListener startAgingListener;
    public void setOnStartAgingListener(OnStartAgingListener l) { this.startAgingListener = l; }

    serverUrl 상수: private final String serverUrl = "http://10.0.2.2:8080"  (BasicEmoAdapter와 동일)

    ViewHolder 필드 (item_jelly_drawer.xml에서 변경된 뷰):
    ImageView emo1ImageView, emo2ImageView;
    TextView tvCreateDate, tvAgingStatus;
    Button btnStartAging;
    (tvJellyId, ivJellyIcon 제거)

    onBindViewHolder 구현:
    1. tvCreateDate.setText(jelly.getCreateDate())
    2. Glide로 emo1ImageView 로딩 (BasicEmoAdapter 패턴 — serverUrl + jelly.getEmo1Icon(), placeholder/error 동일)
    3. Glide로 emo2ImageView 로딩 (serverUrl + jelly.getEmo2Icon())
    4. status 배지 3-state 처리:
       Context ctx = holder.itemView.getContext();
       String status = jelly.getStatus();
       int bgColor, textColor;
       String statusText;
       if ("AGING".equals(status)) {
           bgColor = R.color.badge_aging_bg; textColor = R.color.badge_aging_text;
           statusText = ctx.getString(R.string.status_aging);
           holder.btnStartAging.setVisibility(View.GONE);
       } else if ("MATURED".equals(status)) {
           bgColor = R.color.badge_complete_bg; textColor = R.color.badge_complete_text;
           statusText = ctx.getString(R.string.status_complete);
           holder.btnStartAging.setVisibility(View.GONE);
       } else {
           // WAITING 또는 null/unknown → fallback
           bgColor = R.color.badge_waiting_bg; textColor = R.color.badge_waiting_text;
           statusText = ctx.getString(R.string.status_waiting);
           holder.btnStartAging.setVisibility(View.VISIBLE);
       }
       holder.tvAgingStatus.setText(statusText);
       holder.tvAgingStatus.setTextColor(ContextCompat.getColor(ctx, textColor));
       // GradientDrawable mutate — UI-SPEC 주의사항: setBackgroundColor가 아닌 mutate+setColor 사용
       android.graphics.drawable.Drawable bg = holder.tvAgingStatus.getBackground();
       if (bg instanceof android.graphics.drawable.GradientDrawable) {
           ((android.graphics.drawable.GradientDrawable) bg.mutate())
               .setColor(ContextCompat.getColor(ctx, bgColor));
       } else {
           holder.tvAgingStatus.setBackgroundColor(ContextCompat.getColor(ctx, bgColor));
       }
    5. btnStartAging 클릭 리스너:
       holder.btnStartAging.setOnClickListener(v -> {
           if (startAgingListener != null) startAgingListener.onStartAging(jelly.getJellyId());
       });

    import 목록 확인 필요:
    - androidx.core.content.ContextCompat
    - androidx.recyclerview.widget.DiffUtil, ListAdapter
    - com.bumptech.glide.Glide
    - android.graphics.drawable.GradientDrawable
    - java.util.Objects
  </action>
  <verify>
    <automated>cd c:\Users\KTDS\git\mindJellyAndroid && gradlew.bat compileDebugSources 2>&1 | tail -5</automated>
  </verify>
  <acceptance_criteria>
    - JellyDrawerAdapter.java에 "extends ListAdapter" 선언이 존재한다
    - JellyDrawerAdapter.java에 "interface OnStartAgingListener" 선언이 존재한다
    - JellyDrawerAdapter.java에 "setOnStartAgingListener" 메서드가 존재한다
    - JellyDrawerAdapter.java에 "GradientDrawable" 참조 또는 배지 색상 변경 로직이 존재한다
    - JellyDrawerAdapter.java에 "submitList" 호출을 받을 수 있는 ListAdapter 구조가 있고 setJellyList/notifyDataSetChanged가 제거됐다
    - JellyDrawerAdapter.java에 "emo1ImageView"와 "emo2ImageView" ViewHolder 필드가 존재한다
    - ./gradlew compileDebugSources가 BUILD SUCCESSFUL로 종료된다
  </acceptance_criteria>
  <done>JellyDrawerAdapter ListAdapter 마이그레이션 완료, Glide 감정 아이콘 로딩, 3-state 배지, OnStartAgingListener 인터페이스 완성(DRAW-01/02/03 Adapter 측)</done>
</task>

</tasks>

<threat_model>
## Trust Boundaries

| Boundary | Description |
|----------|-------------|
| Adapter → Activity 콜백 | OnStartAgingListener.onStartAging(jellyId) — jellyId null 가능성 |
| Glide URL 조합 | serverUrl + emoIcon이 null이면 Glide error drawable로 폴백 |

## STRIDE Threat Register

| Threat ID | Category | Component | Disposition | Mitigation Plan |
|-----------|----------|-----------|-------------|-----------------|
| T-02C-01 | Tampering | btnStartAging 클릭 — 이미 AGING/MATURED 상태인 아이템 | mitigate | btnStartAging은 WAITING 상태에서만 VISIBLE — UI 수준 방어. 서버는 PATCH 요청 자체를 검증해야 함 |
| T-02C-02 | Information Disclosure | status null 폴백 | accept | null/unknown을 WAITING으로 폴백하면 사용자가 숙성 버튼을 볼 수 있지만 API가 거부함 — 낮은 위험 |
</threat_model>

<verification>
Wave 2-C 완료 기준:
- ./gradlew compileDebugSources → BUILD SUCCESSFUL
- item_jelly_drawer.xml에 emo1ImageView 존재: grep emo1ImageView app/src/main/res/layout/item_jelly_drawer.xml
- item_jelly_drawer.xml에 tvJellyId 미존재: grep tvJellyId app/src/main/res/layout/item_jelly_drawer.xml (결과 없어야 함)
- JellyDrawerAdapter에 ListAdapter 선언: grep "extends ListAdapter" JellyDrawerAdapter.java
- JellyDrawerAdapter에 OnStartAgingListener: grep OnStartAgingListener JellyDrawerAdapter.java
</verification>

<success_criteria>
- item_jelly_drawer.xml: emo1ImageView(40dp), emo2ImageView(40dp, marginStart=4dp), tvCreateDate(16sp bold), tvAgingStatus(badge), btnStartAging(gone) 존재 / ivJellyIcon, tvJellyId 없음
- JellyDrawerAdapter: ListAdapter extends, DiffUtil.ItemCallback, submitList() 지원
- Glide: emo1Icon/emo2Icon 각각 serverUrl 접두사 + placeholder/error
- 3-state 배지: WAITING/AGING/MATURED 각각 색상 + 텍스트 분기, GradientDrawable mutate 패턴
- btnStartAging: WAITING만 VISIBLE, 클릭 → OnStartAgingListener.onStartAging(jellyId)
- 전체 컴파일 통과
</success_criteria>

<output>
완료 후 .planning/phases/02-핵심루프/02-C-SUMMARY.md 생성
</output>
