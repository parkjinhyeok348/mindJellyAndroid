# Phase 2: 핵심 루프 - Research

**Researched:** 2026-05-14
**Domain:** Android Native Java — MVVM + Retrofit2 + LiveData, RecyclerView ListAdapter, Glide image loading
**Confidence:** HIGH (코드베이스 직접 검증, 기존 패턴 확인 완료)

---

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions

- **D-01:** `JellyDrawerResDTO` 감정 필드(emo1Name, emo1Icon, emo2Name, emo2Icon) 백엔드 이미 준비됨 — 별도 협의 불필요, 바로 DTO 추가 후 사용.
- **D-02:** 감정 아이콘 형식: 이미지 URL (String) → Glide로 ImageView에 로딩. 로컬 drawable 리소스 ID 아님.
- **D-03:** startAging API: `PATCH /api/jelly/{id}`, body에 status 필드 업데이트. 응답 형태 상관없이 isSuccessful() 체크 후 에이징룸으로 이동.
- **D-04:** 프리뷰 트리거: 감정 2개 모두 선택된 순간 즉시 API 호출 (debounce/버튼 없음).
- **D-05:** 프리뷰 엔드포인트: `GET /jellyComb/jelly-icon/{firstEmo}/{secondEmo}/false` — `isAwaken` 파라미터는 false 고정.
- **D-06:** 응답 타입: `String` (이미지 URL) → Glide로 프리뷰 ImageView에 직접 로딩.
- **D-07:** 프리뷰 표시 내용: 이미지만 (AgedEmo 이름 없음). 이름 표시는 v2 검토.

### Claude's Discretion

- **로딩 인디케이터 구현 (QUAL-01):** `Resource<T>`는 success/error만 있음. loading 상태 추가 방식(Resource에 LOADING 추가 vs 별도 `MutableLiveData<Boolean> isLoading`) 중 더 적합한 방식 선택.
- **에러 메시지 한국어화 (QUAL-02):** 공통 유틸 또는 각 Activity 내 switch 처리 중 코드 중복을 최소화하는 방식 선택.
- **회전 버그 (LiveData 캐싱):** `if (liveData != null) return` 패턴으로 ViewModel에서 중복 호출 방지. Researcher가 기존 ViewModel 패턴 확인 후 일관성 있게 적용.
- **감정 선택 UI:** BasicEmoAdapter + RecyclerView 기존 구조 유지. 2개 선택 제한 로직은 Adapter 또는 ViewModel 중 적합한 위치 결정.

### Deferred Ideas (OUT OF SCOPE)

- **합성 AgedEmo 이름 표시:** 프리뷰에 이름 추가는 v2 검토 — MVP는 이미지만 (D-07)
- **일기 글자 수 표시:** REQUIREMENTS에 없음, v2 검토
- **숙성 후 성찰 일기:** Out of Scope (REQUIREMENTS.md)
- **AgingRoom 카운트다운 / JellyMuseum 컬렉션:** Phase 3, 4 범위
</user_constraints>

---

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| JELLY-01 | 감정 목록 그리드에서 젤리 2개를 선택할 수 있다 (감정 이름 + 아이콘 표시) | BasicEmoAdapter 2개 선택 로직 이미 구현됨. 이름 TextView만 item_basic_emo.xml + onBindViewHolder에 추가하면 됨. BasicEmoResDTO에 emoName 필드 이미 존재. |
| JELLY-02 | 감정 2개 선택 시 합성 젤리 프리뷰가 실시간으로 표시된다 | TodayJellyActivity의 fetchCombinedJellyIcon() 이미 구현됨. 선택 즉시 호출, Glide로 combinedJellyImageView에 로딩. JellyCombService.getJellyIcon() 선언 완료. |
| JELLY-03 | 사용자가 자유 텍스트로 오늘의 일기를 작성할 수 있다 | activity_today_jelly.xml에 EditText 미존재 — XML 추가 + ViewBinding 연결 필요. |
| JELLY-04 | 저장 버튼 클릭 시 젤리가 저장되고 성공 피드백이 표시된다 | JellyService.createJelly(@Body JellySaveReqDTO) 이미 선언됨. JellyViewModel.createJelly() 존재. TodayJellyActivity에 버튼 + observe 연결만 필요. JellySaveReqDTO 필드 분석 필요 (jellyCombId 취득 경로 결정). |
| JELLY-05 | 저장 완료 후 자동으로 젤리서랍 화면으로 이동한다 | createJelly 성공 콜백에서 startActivity(JellyDrawerActivity) + finish() 패턴. |
| DRAW-01 | 사용자가 날짜별 과거 기록 목록을 조회할 수 있다 (날짜 + 감정 아이콘 2개 포함) | JellyDrawerResDTO에 emo1Icon, emo2Icon 필드 추가 필요. JellyDrawerAdapter의 onBindViewHolder에 두 ImageView를 Glide로 로딩. |
| DRAW-02 | 각 기록에 숙성 상태 배지가 표시된다 (대기중 / 숙성중 / 숙성완료) | 현재 isAging Boolean만 존재 — 상태값(status String 또는 enum)이 백엔드 응답에 포함되는지 확인 필요. 기존 tvAgingStatus TextView는 존재함. |
| DRAW-03 | "숙성시키기" 버튼 클릭 시 startAging API 호출 후 에이징룸으로 이동 | PATCH /api/jelly/{id} 신규 메서드 추가 필요 (JellyService + JellyRepository + JellyViewModel). item_jelly_drawer.xml에 버튼 추가 + Adapter 클릭 리스너 패턴 필요. |
| DRAW-04 | 기록이 없을 때 빈 상태 안내 메시지가 표시된다 | getJellyList 결과가 빈 리스트일 때 emptyView 표시 로직. activity_jelly_drawer.xml에 empty state View 추가. |
| QUAL-01 | 모든 네트워크 요청 중 로딩 인디케이터가 표시된다 | Resource<T>에 LOADING 상태 없음 — 별도 isLoading LiveData<Boolean> 패턴 권장 (기존 코드 파괴 없음). |
| QUAL-02 | API 에러 발생 시 한국어 사용자 친화적 메시지가 표시된다 | 현재 에러 메시지: "Error: " + response.message() (영문). 공통 변환 유틸 신규 생성 권장. |
</phase_requirements>

---

## Summary

Phase 2는 기존 코드베이스가 상당 부분 준비된 상태에서 시작한다. TodayJellyActivity는 BasicEmoAdapter의 2개 선택 + 합성 프리뷰 API 호출까지 이미 구현되어 있다. 빠진 것은 (1) 감정 이름 표시 (item_basic_emo.xml TextView 추가), (2) 일기 EditText + 저장 버튼 + JellySaveReqDTO 조립 로직, (3) 저장 성공 후 JellyDrawerActivity 이동이다.

JellyDrawerActivity 측에서는 JellyDrawerResDTO에 감정 필드(emo1Icon, emo2Icon 등)를 추가하고, Adapter를 ListAdapter로 마이그레이션하며, "숙성시키기" 버튼의 PATCH API 연결, 빈 상태 UI를 완성해야 한다. startAging을 위한 새 Retrofit 메서드(PATCH)가 JellyService에 추가되어야 한다.

공통 품질 요건(QUAL-01 로딩, QUAL-02 한국어 에러)은 기존 Resource<T> 구조의 한계(success/error만 존재)로 인해 설계 결정이 필요하다. 별도 `MutableLiveData<Boolean> isLoading`을 각 ViewModel에 추가하는 방식이 기존 코드 파괴 없이 가장 일관성 있는 접근이다.

**Primary recommendation:** 기존 패턴(AndroidViewModel + Repository + enqueue/postValue + Glide)을 그대로 따르면서 누락된 필드/메서드/UI 요소만 추가한다. 새 추상화 레이어 도입 금지.

---

## Architectural Responsibility Map

| Capability | Primary Tier | Secondary Tier | Rationale |
|------------|-------------|----------------|-----------|
| 감정 목록 표시 (JELLY-01) | View (TodayJellyActivity) | ViewModel (BasicEmoViewModel) | RecyclerView + Adapter가 렌더링, ViewModel이 데이터 캐싱 |
| 합성 프리뷰 (JELLY-02) | View (TodayJellyActivity) | ViewModel (JellyCombViewModel) | Adapter 콜백 → ViewModel.getJellyIcon() → Glide |
| 일기 작성/저장 (JELLY-03/04/05) | View (TodayJellyActivity) | ViewModel (JellyViewModel) | EditText 입력 수집 → DTO 조립 → createJelly() observe |
| 젤리서랍 목록 (DRAW-01/02) | View (JellyDrawerActivity) | ViewModel (JellyViewModel) | getJellyList() observe → Adapter submitList() |
| 숙성시키기 (DRAW-03) | View (JellyDrawerAdapter) | ViewModel (JellyViewModel) | Adapter item click → Activity callback → startAging() |
| 빈 상태 (DRAW-04) | View (JellyDrawerActivity) | — | 리스트 크기 0 체크 → emptyView visibility 전환 |
| 로딩 인디케이터 (QUAL-01) | View (각 Activity) | ViewModel (isLoading LiveData) | ViewModel.isLoading.observe → ProgressBar visibility |
| 한국어 에러 (QUAL-02) | View (각 Activity) | common/ErrorMessageUtil | Resource.getError() → 유틸 변환 → Toast |

---

## Standard Stack

### Core (이미 프로젝트에 포함됨)

| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Retrofit2 | 2.9.0 | HTTP API 호출 | 기존 전 도메인 사용 [VERIFIED: app/build.gradle] |
| OkHttp3 | 4.9.0 | HTTP 클라이언트 + AuthInterceptor | 기존 사용 [VERIFIED: libs.versions.toml] |
| Gson | 2.10.1 | JSON 직렬화/역직렬화 | 기존 converter-gson 사용 [VERIFIED: libs.versions.toml] |
| Glide | 4.15.1 | 이미지 URL → ImageView 비동기 로딩 | 기존 BasicEmoAdapter, TodayJellyActivity에서 사용 [VERIFIED: 코드베이스] |
| AndroidX Lifecycle (LiveData/ViewModel) | 2.6.1 | MVVM LiveData observe 패턴 | 기존 전 도메인 AndroidViewModel [VERIFIED: libs.versions.toml] |
| ViewBinding | — | XML View 참조 타입 안전 접근 | TodayJellyActivity에서 이미 사용 (ActivityTodayJellyBinding) [VERIFIED: 코드베이스] |
| RecyclerView | — | 목록 표시 | 기존 JellyDrawerAdapter, BasicEmoAdapter [VERIFIED: 코드베이스] |

### Supporting

| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| ListAdapter + DiffUtil | AndroidX RecyclerView 내장 | 효율적 리스트 갱신, 애니메이션 | JellyDrawerAdapter, BasicEmoAdapter 마이그레이션 시 |
| ProgressBar (Android SDK) | — | 로딩 인디케이터 UI 요소 | QUAL-01 구현 시 각 Activity XML에 추가 |

### Alternatives Considered

| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| 별도 isLoading LiveData | Resource<T>에 LOADING 상태 추가 | Resource 추가 시 기존 모든 observe 코드 수정 필요 — isLoading LiveData가 덜 파괴적 |
| Adapter 내부 클릭 처리 | ViewModel에서 선택 상태 관리 | 2개 선택 제한 로직이 이미 Adapter에 구현되어 있으므로 Adapter 유지가 일관성 높음 |
| startAging 결과 Resource<T> | 단순 Boolean LiveData | 일관성 차원에서 Resource<Void> 또는 Resource<ResponseBody> 패턴 유지 권장 |

---

## Architecture Patterns

### System Architecture Diagram

```
[TodayJellyActivity]
    |-- BasicEmoViewModel.getAllBasicEmoList() --> BasicEmoRepository --> BasicEmoService
    |       observe --> BasicEmoAdapter.setEmos() --> RecyclerView 그리드
    |
    |-- BasicEmoAdapter.OnSelectionChangedListener (selectedEmos.size() == 2)
    |       --> JellyCombViewModel.getJellyIcon(id1, id2, false)
    |               --> JellyCombRepository --> JellyCombService (GET /jellyComb/jelly-icon/{}/{}/false)
    |               observe --> Glide.load(serverUrl + iconPath) --> combinedJellyImageView
    |
    |-- 저장 버튼 onClick
            --> JellyViewModel.createJelly(JellySaveReqDTO)
                    --> JellyRepository --> JellyService (POST /jelly)
                    observe success --> Toast + startActivity(JellyDrawerActivity)
                    observe error   --> 한국어 Toast

[JellyDrawerActivity]
    |-- JellyViewModel.getJellyList(userId) --> JellyRepository --> GET jelly/user/{userId}
    |       observe success --> JellyDrawerAdapter.submitList()
    |               --> item 렌더: tvCreateDate, tvAgingStatus, Glide(emo1Icon), Glide(emo2Icon)
    |       observe empty   --> emptyView visible
    |       observe error   --> 한국어 Toast
    |
    |-- "숙성시키기" 버튼 클릭 (Adapter → Activity 콜백)
            --> JellyViewModel.startAging(jellyId)
                    --> JellyRepository --> JellyService (PATCH /api/jelly/{id})
                    observe success --> startActivity(AgingRoomActivity)
                    observe error   --> 한국어 Toast

[공통]
    ViewModel.isLoading: MutableLiveData<Boolean>
        postValue(true) before enqueue
        postValue(false) in onResponse/onFailure
        observe --> ProgressBar visibility VISIBLE/GONE
```

### Recommended Project Structure (변경 없음, 기존 패키지 유지)

```
basicEmo/
├── model/BasicEmoResDTO.java          # 기존 (emoId, emoName, emoIcon 모두 존재)
├── retrofit/BasicEmoRepository.java   # 기존
├── viewmodel/BasicEmoViewModel.java   # 기존
└── view/
    ├── BasicEmoAdapter.java           # 수정: ListAdapter + TextView 이름 표시
    └── TodayJellyActivity.java        # 수정: EditText + 저장 버튼 + JellyViewModel observe

jellyDomain/jelly/
├── model/
│   ├── JellyDrawerResDTO.java         # 수정: emo1Name, emo1Icon, emo2Name, emo2Icon 추가
│   ├── JellySaveReqDTO.java           # 기존 (jellyCombId, content 포함)
│   └── JellyStartAgingReqDTO.java     # 신규: status 필드 (PATCH body)
├── retrofit/
│   ├── JellyService.java              # 수정: PATCH /api/jelly/{id} 추가
│   └── JellyRepository.java          # 수정: startAging() 추가
├── viewmodel/JellyViewModel.java      # 수정: startAging() + isLoading 추가
└── view/
    ├── JellyDrawerAdapter.java        # 수정: ListAdapter + emo 아이콘 + 숙성시키기 버튼
    └── JellyDrawerActivity.java       # 수정: startAging 콜백, emptyView, isLoading observe

common/
└── ErrorMessageUtil.java              # 신규: HTTP 에러코드 → 한국어 메시지 변환

res/layout/
├── item_basic_emo.xml                 # 수정: TextView(emoName) 추가
├── item_jelly_drawer.xml              # 수정: emo1ImageView, emo2ImageView, 숙성시키기 Button 추가
└── activity_today_jelly.xml          # 수정: EditText(diary) + Button(save) + ProgressBar 추가
    activity_jelly_drawer.xml         # 수정: emptyView TextView + ProgressBar 추가
```

### Pattern 1: AndroidViewModel + Repository + enqueue/postValue (기존 패턴)

**What:** ViewModel이 Repository를 직접 생성, Repository가 enqueue() 콜백에서 MutableLiveData.postValue() 호출
**When to use:** 모든 API 연동 메서드

```java
// Source: 기존 JellyRepository.java 패턴
public LiveData<Resource<Jelly>> createJelly(JellySaveReqDTO reqDTO) {
    MutableLiveData<Resource<Jelly>> liveData = new MutableLiveData<>();
    jellyService.createJelly(reqDTO).enqueue(new Callback<Jelly>() {
        @Override
        public void onResponse(Call<Jelly> call, Response<Jelly> response) {
            if (response.isSuccessful()) {
                liveData.postValue(Resource.success(response.body()));
            } else {
                liveData.postValue(Resource.error("Error: " + response.message()));
            }
        }
        @Override
        public void onFailure(Call<Jelly> call, Throwable t) {
            liveData.postValue(Resource.error(t.getMessage()));
        }
    });
    return liveData;
}
```

### Pattern 2: 로딩 인디케이터 — 별도 isLoading LiveData (QUAL-01 권장 구현)

**What:** Resource<T>를 건드리지 않고 ViewModel에 `MutableLiveData<Boolean> isLoading` 필드 추가
**When to use:** QUAL-01 구현, 기존 observe 코드 파괴 최소화

```java
// Source: [ASSUMED] — Android MVVM 표준 패턴
// ViewModel 내부
public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

public LiveData<Resource<Jelly>> createJelly(JellySaveReqDTO reqDTO) {
    isLoading.postValue(true);
    // ... Repository 호출, 완료 시 isLoading.postValue(false)
}

// Activity 내부
jellyViewModel.isLoading.observe(this, loading -> {
    progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
});
```

### Pattern 3: ListAdapter + DiffUtil (JellyDrawerAdapter, BasicEmoAdapter 마이그레이션)

**What:** RecyclerView.Adapter 대신 ListAdapter를 사용, DiffUtil.ItemCallback으로 변경 감지
**When to use:** 데이터 갱신 시 notifyDataSetChanged() 제거, 효율적 부분 갱신

```java
// Source: [ASSUMED] — Android RecyclerView ListAdapter 표준 패턴
public class JellyDrawerAdapter extends ListAdapter<JellyDrawerResDTO, JellyDrawerAdapter.ViewHolder> {

    public JellyDrawerAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<JellyDrawerResDTO> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<JellyDrawerResDTO>() {
            @Override
            public boolean areItemsTheSame(@NonNull JellyDrawerResDTO a, @NonNull JellyDrawerResDTO b) {
                return a.getJellyId().equals(b.getJellyId());
            }
            @Override
            public boolean areContentsTheSame(@NonNull JellyDrawerResDTO a, @NonNull JellyDrawerResDTO b) {
                return a.equals(b);  // equals() override 필요
            }
        };

    // submitList(list) 호출로 갱신 (notifyDataSetChanged 불필요)
}
```

### Pattern 4: Adapter → Activity 클릭 콜백 (숙성시키기 버튼)

**What:** Adapter가 OnItemActionListener 인터페이스 선언, Activity가 구현
**When to use:** RecyclerView 아이템 내 버튼 클릭을 Activity/Fragment로 전달할 때

```java
// Source: [ASSUMED] — Android RecyclerView 표준 콜백 패턴
// Adapter 내부
public interface OnStartAgingListener {
    void onStartAging(Long jellyId);
}
private OnStartAgingListener listener;
public void setOnStartAgingListener(OnStartAgingListener l) { this.listener = l; }

// onBindViewHolder 내부
btnStartAging.setOnClickListener(v -> {
    if (listener != null) listener.onStartAging(jelly.getJellyId());
});

// Activity 내부
adapter.setOnStartAgingListener(jellyId -> {
    jellyViewModel.startAging(jellyId).observe(this, resource -> { ... });
});
```

### Pattern 5: Glide 이미지 URL 로딩 (기존 패턴)

**What:** Glide.with(context).load(url).placeholder(...).into(imageView)
**When to use:** emo1Icon, emo2Icon URL → ImageView 로딩

```java
// Source: 기존 BasicEmoAdapter.java, TodayJellyActivity.java
// 주의: BasicEmoAdapter는 serverUrl("http://10.0.2.2:8080")을 하드코딩하여 앞에 붙임
// JellyDrawerResDTO의 icon URL이 상대경로인지 절대경로인지 확인 필요
Glide.with(holder.itemView.getContext())
        .load(fullImageUrl)  // 절대 URL이면 그대로, 상대경로면 serverUrl + emoIcon
        .placeholder(android.R.drawable.ic_menu_report_image)
        .error(android.R.drawable.stat_notify_error)
        .into(holder.emo1ImageView);
```

### Pattern 6: LiveData 캐싱 — 회전 버그 방지

**What:** ViewModel 내부에 결과 LiveData를 캐싱하여 화면 회전 시 재호출 방지
**When to use:** 목록 조회처럼 한 번만 호출되어야 하는 데이터 (getJellyList)

```java
// Source: [ASSUMED] — Android ViewModel lifecycle 표준 패턴
// 주의: TodayJellyActivity의 fetchCombinedJellyIcon()은 선택 변경마다 새로 호출해야 하므로
//       캐싱 패턴이 아닌 매번 새 LiveData를 반환하는 현재 방식이 올바름
private LiveData<Resource<List<JellyDrawerResDTO>>> jellyListLiveData;

public LiveData<Resource<List<JellyDrawerResDTO>>> getJellyList(long userId) {
    if (jellyListLiveData == null) {
        jellyListLiveData = repository.getJellyList(userId);
    }
    return jellyListLiveData;
}
```

### Anti-Patterns to Avoid

- **notifyDataSetChanged() 남용:** ListAdapter 마이그레이션 후에도 setJellyList()에 notifyDataSetChanged() 유지하는 패턴 — submitList()로 교체
- **Activity에서 직접 Retrofit 호출:** Repository/ViewModel 우회 — 기존 패턴 위반
- **Context 없는 RetrofitClient.createService():** deprecated createService(Class) 시그니처 사용 금지 — 반드시 createService(Class, context) 사용
- **JellySaveReqDTO에 null jellyCombId 전달:** 합성 프리뷰 성공 후 서버 응답에서 jellyCombId를 얻거나, JellyCombResDTO에서 조합 ID를 캐싱해야 함
- **숙성 상태를 isAging Boolean으로만 표현:** DRAW-02는 "대기중/숙성중/숙성완료" 3단계 — isAging Boolean만으로는 부족. 백엔드 status 필드 포함 여부 확인 필요

---

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| 이미지 URL → ImageView 비동기 로딩 | 커스텀 AsyncTask/Thread | Glide 4.15.1 (기존 의존성) | 캐싱, 메모리 관리, placeholder, error 처리 내장 |
| RecyclerView 리스트 효율적 갱신 | 수동 notifyItemChanged 계산 | ListAdapter + DiffUtil | DiffUtil이 O(N) diff 알고리즘으로 최소 변경만 애니메이션 |
| HTTP 요청 헤더 주입 | Activity마다 토큰 헤더 추가 | 기존 AuthInterceptor | Phase 1에서 완성, 모든 요청에 자동 적용 |

**Key insight:** 이 프로젝트는 이미 모든 핵심 라이브러리가 세팅되어 있다. Phase 2에서 신규 의존성 추가는 불필요하다.

---

## Common Pitfalls

### Pitfall 1: JellySaveReqDTO의 jellyCombId 취득 경로 불명확

**What goes wrong:** 저장 버튼 클릭 시 JellySaveReqDTO에 jellyCombId를 채워야 하는데, 프리뷰 API(`GET /jellyComb/jelly-icon/{}/{}/false`)는 String(이미지 URL)만 반환하고 jellyCombId를 반환하지 않는다.
**Why it happens:** 합성 프리뷰 엔드포인트(D-05/D-06)의 응답이 URL String뿐이다. JellySaveReqDTO 서명에는 jellyCombId가 필수 필드로 존재한다.
**How to avoid:** 두 가지 선택지 중 플래너가 결정해야 한다:
  1. 별도 `GET /jellyComb/jelly-icon/{firstEmo}/{secondEmo}/{isAwaken}` 응답에 jellyCombId가 포함되도록 백엔드 변경 요청
  2. `GET /jellyComb/jelly-comb-id/{firstEmo}/{secondEmo}` 같은 별도 조회 엔드포인트가 이미 존재하는지 확인 (JellyCombService.getJellyCombById()는 id로 조회하는 역방향)
  3. 임시 방편: jellyName에 "emoji1_emoji2" 형태로 저장하고 jellyCombId는 null (API 수락 여부 확인 필요)
**Warning signs:** TodayJellyActivity에서 저장 버튼 클릭 시 jellyCombId가 null이면 API 400/500 에러 발생.

### Pitfall 2: fetchCombinedJellyIcon()에서 LiveData 누적 관찰 문제

**What goes wrong:** 현재 TodayJellyActivity.fetchCombinedJellyIcon()이 선택할 때마다 `jellyCombViewModel.getJellyIcon(...).observe(this, ...)` 를 반복 호출한다. 동일 LifecycleOwner로 같은 LiveData를 여러 번 observe()하면 Observer가 중복 등록될 수 있다.
**Why it happens:** getJellyIcon()이 매번 새 MutableLiveData를 생성하여 반환하므로 기술적으로 다른 LiveData이지만, 이전 관찰이 정리되지 않으면 메모리 누수 위험이 있다.
**How to avoid:** ViewModel에 `previewLiveData` 캐시를 두고, MediatorLiveData 또는 switchMap을 사용하거나, 선택된 감정 ID를 LiveData<Pair<Long,Long>>로 관리하여 Transformation.switchMap으로 자동 전환한다. 또는 현재 방식 유지 시 removeObservers() 후 재observe.
**Warning signs:** 앱 로그에 같은 이미지 URL로 Glide 요청이 여러 번 발생.

### Pitfall 3: 숙성 상태 표현 — isAging Boolean vs 3단계 status

**What goes wrong:** DRAW-02는 "대기중 / 숙성중 / 숙성완료" 3단계 배지를 요구한다. 현재 JellyDrawerResDTO의 isAging은 Boolean이므로 "대기중"(false)과 "숙성완료"(완료 후 false)를 구분할 수 없다.
**Why it happens:** 백엔드가 status String("WAITING", "AGING", "MATURED") 또는 isMatured Boolean을 별도로 내려주지 않으면 클라이언트에서 상태를 정확히 표현할 수 없다.
**How to avoid:** D-01에서 백엔드가 감정 필드를 준비했다고 했으므로, status 필드도 함께 JellyDrawerResDTO에 포함되는지 백엔드 응답 구조를 확인해야 한다. 플래너는 이를 Wave 0 확인 항목으로 포함시켜야 한다.
**Warning signs:** DRAW-02 테스트 시 숙성완료 상태가 "대기중"과 동일하게 표시됨.

### Pitfall 4: ViewBinding 미사용 화면에서 findViewById 혼재

**What goes wrong:** TodayJellyActivity는 ViewBinding(`ActivityTodayJellyBinding`) 사용 중이고, JellyDrawerActivity는 findViewById 방식이다. 동일 Phase에서 두 패턴이 섞이면 코드 일관성이 떨어진다.
**How to avoid:** JellyDrawerActivity도 ViewBinding으로 전환하거나, 기존 방식 유지. build.gradle에 `viewBinding true` 이미 활성화됨 — ViewBinding 전환 권장.

### Pitfall 5: serverUrl 하드코딩 분산

**What goes wrong:** BasicEmoAdapter와 TodayJellyActivity에 `private final String serverUrl = "http://10.0.2.2:8080"` 이 분산되어 있다. JellyDrawerAdapter에서도 동일한 하드코딩이 추가되면 유지보수성 저하.
**How to avoid:** RetrofitClient.BASE_URL을 `public static final`로 노출하거나 별도 상수 클래스에 정의한다. Phase 2에서는 기존 방식과 일관성 유지(각 클래스에 하드코딩)를 선택하거나, 공통 상수 추출 중 하나를 선택.

---

## Code Examples

### JellySaveReqDTO 조립 (TodayJellyActivity 저장 버튼)

```java
// Source: 기존 JellySaveReqDTO.java 분석 + [ASSUMED] 조립 패턴
// 필수 필드: userId, jellyCombId, content, createDate
// jellyName: MVP에서는 "감정1+감정2" 형태 자동 생성 또는 고정값 사용 가능
// agingPeriod: "7" (일 단위) 또는 백엔드 기본값 사용 여부 확인 필요
// jellyImages: MVP에서는 null 또는 빈 리스트 (이미지 첨부 기능 없음)
String diaryContent = binding.etDiary.getText().toString().trim();
String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
Long userId = SessionManager.getInstance(this).getUserId();
// jellyCombId는 Pitfall 1에서 언급한 방식으로 취득 필요

JellySaveReqDTO reqDTO = new JellySaveReqDTO(
    userId,
    jellyCombId,       // 합성 프리뷰 결과에서 캐싱된 값
    "오늘의 젤리",      // jellyName: 자동 생성 또는 사용자 입력
    diaryContent,
    "7",               // agingPeriod: 7일 고정
    today,
    null               // jellyImages: MVP는 null
);
```

### PATCH startAging — JellyService 추가

```java
// Source: D-03 결정, [ASSUMED] Retrofit PATCH 선언 패턴
// JellyService.java에 추가
import retrofit2.http.PATCH;
import retrofit2.http.Body;

@PATCH("/api/jelly/{jellyId}")
Call<ResponseBody> startAging(@Path("jellyId") Long jellyId, @Body JellyStartAgingReqDTO reqDTO);

// JellyStartAgingReqDTO: { "status": "AGING" } 또는 백엔드 요구 필드 확인
```

### JellyDrawerResDTO 감정 필드 추가

```java
// Source: D-01, D-02 결정 반영
// JellyDrawerResDTO.java에 추가할 필드들
@SerializedName("emo1Name")
private String emo1Name;
@SerializedName("emo1Icon")
private String emo1Icon;  // 이미지 URL (String)
@SerializedName("emo2Name")
private String emo2Name;
@SerializedName("emo2Icon")
private String emo2Icon;  // 이미지 URL (String)
```

### 빈 상태 처리 (JellyDrawerActivity)

```java
// Source: [ASSUMED] — Android empty state 표준 패턴
jellyViewModel.getJellyList(userId).observe(this, resource -> {
    if (resource.isSuccess()) {
        List<JellyDrawerResDTO> list = resource.getData();
        if (list == null || list.isEmpty()) {
            rvJellyList.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvJellyList.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
            adapter.submitList(list);
        }
    }
});
```

---

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| RecyclerView.Adapter + notifyDataSetChanged() | ListAdapter + DiffUtil + submitList() | RecyclerView 1.2.0+ | 부분 갱신 애니메이션, 성능 향상 |
| ViewModel extends ViewModel (Context 없음) | AndroidViewModel (Application 자동 주입) | Phase 1에서 전환 완료 | Repository 생성에 Context 불필요 |

**Deprecated/outdated:**

- `RetrofitClient.createService(Class)` (no-arg): deprecated 마킹됨 — 반드시 `createService(Class, context)` 사용
- `JellyDrawerActivity`의 `Long userId = sessionManager.getUserId()` 에서 `Long` 타입으로 박싱: `getUserId()`는 `long` 프리미티브 반환 — 박싱 불필요하지만 동작에는 문제 없음 (자동 박싱)

---

## Assumptions Log

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| A1 | 별도 isLoading MutableLiveData<Boolean> 패턴이 Resource<T>에 LOADING 추가보다 파괴적 변경이 적다 | QUAL-01 구현 패턴 | Resource에 LOADING을 추가하면 기존 observe 코드(isSuccess()/isError() 체크)를 모두 수정해야 함 |
| A2 | JellyDrawerResDTO의 emo1Icon, emo2Icon URL이 BasicEmoAdapter와 동일하게 상대경로이므로 serverUrl 앞에 붙여야 함 | Glide 로딩 예시 | 절대 URL이면 serverUrl 접두사 없이 직접 사용해야 함 |
| A3 | JellySaveReqDTO에서 jellyImages를 null로 전달해도 백엔드가 수락한다 | TodayJellyActivity 저장 | null을 수락하지 않으면 빈 리스트 `new ArrayList<>()` 전달 필요 |
| A4 | agingPeriod 필드에 "7" (문자열)을 전달하는 것이 백엔드가 기대하는 형식이다 | JellySaveReqDTO 조립 | 백엔드가 다른 형식(숫자, 날짜 등)을 기대하면 400 에러 |
| A5 | startAging PATCH body에 `{"status": "AGING"}` 형태로 전달한다 | JellyService PATCH | 실제 백엔드 필드명/값이 다를 수 있음 — D-03은 "status 필드 업데이트"라고만 명시 |
| A6 | JellyDrawerResDTO에 숙성 3단계 구분을 위한 status 필드가 백엔드 응답에 존재한다 | DRAW-02 구현 | Boolean isAging만 있으면 "대기중"/"숙성완료" 구분 불가 |

---

## Open Questions

1. **jellyCombId 취득 경로**
   - What we know: 합성 프리뷰 API는 이미지 URL String만 반환. JellySaveReqDTO에는 jellyCombId 필수.
   - What's unclear: jellyCombId를 어떻게 취득할 것인가 — 별도 API? 프리뷰 응답 변경? 프리뷰 없이 저장 시 서버에서 매핑?
   - Recommendation: 플래너가 Wave 0 항목으로 "백엔드에 jellyCombId 반환 확인" 태스크를 추가. 임시방편으로 jellyCombId=null 허용 여부 확인.

2. **숙성 상태 3단계 (DRAW-02)**
   - What we know: 현재 JellyDrawerResDTO에 isAging Boolean만 존재.
   - What's unclear: 백엔드가 status String("WAITING"/"AGING"/"MATURED")을 JellyDrawerResDTO에 함께 내려주는지 여부.
   - Recommendation: D-01에서 백엔드 준비 완료라고 했으므로 DTO에 status 필드 추가 시도. 실패하면 isAging=false를 "대기중"/"숙성완료" 중 어느 것으로 표시할지 결정 필요.

3. **startAging 후 AgingRoomActivity 이동 — AgingRoomActivity 존재 여부**
   - What we know: activity_aging_room.xml 존재 확인됨. AgingRoomActivity.java는 Phase 3 범위.
   - What's unclear: Phase 2에서 startAging 성공 후 AgingRoomActivity로 이동해야 하지만, AgingRoomActivity 내부 구현은 Phase 3이다. 빈 Activity(껍데기)라도 startActivity가 가능한지 확인 필요.
   - Recommendation: AgingRoomActivity.java가 이미 선언되어 있다면 빈 상태로 이동만 구현. 없다면 Phase 2에서 빈 Activity 클래스 생성 필요.

---

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| Android SDK compileSdk | 빌드 | ✓ | 34 | — |
| minSdk | 기기 지원 | ✓ | 31 | — |
| Glide | 이미지 로딩 | ✓ | 4.15.1 | — |
| Retrofit2 | API 호출 | ✓ | 2.9.0 | — |
| ViewBinding | XML 뷰 접근 | ✓ | enabled | — |
| ListAdapter/DiffUtil | RecyclerView 효율화 | ✓ | androidx.recyclerview 내장 | — |
| 백엔드 API | 실제 API 호출 테스트 | 미확인 | — | 에뮬레이터 10.0.2.2:8080 로컬 서버 |

---

## Validation Architecture

### Test Framework

| Property | Value |
|----------|-------|
| Framework | JUnit 4.13.2 + Espresso 3.5.1 (의존성 존재, 설정 파일 없음) |
| Config file | 없음 — Wave 0 설정 필요 없음 (Android 프로젝트 기본 구조) |
| Quick run command | `./gradlew testDebugUnitTest` |
| Full suite command | `./gradlew connectedAndroidTest` |

### Phase Requirements → Test Map

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| JELLY-01 | 감정 이름 + 아이콘 그리드 표시, 2개 선택 제한 | Unit (Adapter 로직) | `./gradlew testDebugUnitTest` | ❌ Wave 0 |
| JELLY-02 | 2개 선택 시 프리뷰 API 호출 | Unit (ViewModel) | `./gradlew testDebugUnitTest` | ❌ Wave 0 |
| JELLY-03 | EditText 텍스트 입력 | UI (Espresso) | `./gradlew connectedAndroidTest` | ❌ Wave 0 |
| JELLY-04 | 저장 버튼 클릭 → 성공 Toast | UI (Espresso) + Manual | `./gradlew connectedAndroidTest` | ❌ Wave 0 |
| JELLY-05 | 저장 성공 후 JellyDrawerActivity 이동 | UI (Espresso) | `./gradlew connectedAndroidTest` | ❌ Wave 0 |
| DRAW-01 | 날짜 + 아이콘 2개 목록 표시 | UI (Espresso) | `./gradlew connectedAndroidTest` | ❌ Wave 0 |
| DRAW-02 | 상태 배지 표시 | Unit (Adapter 로직) | `./gradlew testDebugUnitTest` | ❌ Wave 0 |
| DRAW-03 | 숙성시키기 → API 호출 + 이동 | Manual (실 API 필요) | — | Manual only |
| DRAW-04 | 빈 상태 안내 | Unit (Activity 로직) | `./gradlew testDebugUnitTest` | ❌ Wave 0 |
| QUAL-01 | 네트워크 요청 중 로딩 표시 | Unit (isLoading LiveData) | `./gradlew testDebugUnitTest` | ❌ Wave 0 |
| QUAL-02 | 에러 시 한국어 메시지 | Unit (ErrorMessageUtil) | `./gradlew testDebugUnitTest` | ❌ Wave 0 |

### Sampling Rate

- **Per task commit:** `./gradlew compileDebugSources` (컴파일 오류 없음 확인)
- **Per wave merge:** `./gradlew testDebugUnitTest`
- **Phase gate:** 빌드 성공 + 에뮬레이터 수동 E2E 플로우 확인 (저장 → 서랍 이동) before `/gsd-verify-work`

### Wave 0 Gaps

- 현재 `src/test/` 디렉토리에 프로젝트 전용 테스트 파일 없음 (기본 Android 프로젝트 구조)
- MVP 품질 수준에서 핵심 단위 테스트: BasicEmoAdapter 선택 제한 로직, ErrorMessageUtil 변환 로직
- Espresso E2E는 실 서버 필요로 CI에 포함하기 어려움 — 수동 테스트로 대체

---

## Security Domain

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|-----------------|
| V2 Authentication | 간접 (JWT 이미 처리됨) | Phase 1에서 구현된 SessionManager + AuthInterceptor |
| V3 Session Management | 간접 | Phase 1 완료 |
| V4 Access Control | 아니오 | 서버 측 처리 |
| V5 Input Validation | 예 | 일기 텍스트 빈 값 체크, userId == -1L 체크 |
| V6 Cryptography | 아니오 (Phase 1 완료) | EncryptedSharedPreferences |

### Known Threat Patterns for Android MVVM

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| userId 위조 | Tampering | JWT에서 서버 검증, 클라이언트는 SessionManager에서만 읽기 |
| 빈 일기 저장 | Tampering | 저장 버튼 onClick에서 `etDiary.getText().toString().trim().isEmpty()` 체크 후 거부 |
| 로그아웃 없이 타인 계정 접근 | Spoofing | AuthInterceptor 401 자동 로그아웃 (Phase 1 완료) |

---

## Sources

### Primary (HIGH confidence)

- 코드베이스 직접 검증: `JellyRepository.java`, `JellyCombRepository.java`, `BasicEmoRepository.java` — enqueue/postValue 패턴
- 코드베이스 직접 검증: `TodayJellyActivity.java` — fetchCombinedJellyIcon() 이미 구현됨
- 코드베이스 직접 검증: `BasicEmoAdapter.java` — 2개 선택 제한 로직 이미 구현됨
- 코드베이스 직접 검증: `JellyCombService.java` — getJellyIcon(Long, Long, Boolean) 선언 완료
- 코드베이스 직접 검증: `JellyService.java` — createJelly, getJellyList 선언 완료, PATCH 미선언
- 코드베이스 직접 검증: `Resource.java` — success/error만 존재, LOADING 없음
- 코드베이스 직접 검증: `libs.versions.toml`, `app/build.gradle` — 의존성 버전 확인

### Secondary (MEDIUM confidence)

- 02-CONTEXT.md의 백엔드 결정 사항 (D-01~D-07) — 사용자 확인 완료

### Tertiary (LOW confidence)

- JellyStartAgingReqDTO body 필드명("status") — 백엔드 실제 API 계약 미확인 [ASSUMED]
- jellyCombId 취득 경로 — 백엔드 응답 구조 미확인 [ASSUMED]
- agingPeriod 필드 형식 ("7" 문자열) — 백엔드 기대값 미확인 [ASSUMED]

---

## Metadata

**Confidence breakdown:**

- Standard Stack: HIGH — 모든 의존성 libs.versions.toml + build.gradle 직접 확인
- Architecture: HIGH — 기존 코드베이스 패턴 직접 분석, 신규 패턴은 [ASSUMED] 태깅
- Pitfalls: HIGH — 코드베이스에서 직접 발견된 문제점 (jellyCombId 취득, isAging Boolean 한계)
- Open Questions: MEDIUM — 백엔드 API 계약 상세 미확인

**Research date:** 2026-05-14
**Valid until:** 2026-06-14 (안정적 기술 스택, 코드베이스 변경 없으면 유효)
