# Step 2 Debug Preview Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Step 2(일기 입력 단계)에서 선택된 조합 젤리 이미지, 두 감정 이름, jellyCombId를 화면에 표시해 데이터 흐름을 눈으로 검증한다.

**Architecture:** `activity_today_jelly.xml`에 top constraint를 추가해 Step 2에서도 조합 이미지가 상단에 표시되게 하고, DEBUG 전용 TextView 3개를 추가한다. `TodayJellyActivity`에서 `showStep2()` / `showStep1()`을 수정해 Step 전환 시 해당 뷰의 visibility와 텍스트를 바인딩한다.

**Tech Stack:** Android Java, ViewBinding, ConstraintLayout, BuildConfig

---

## File Map

| Role | File | Change |
|------|------|--------|
| Step 2 디버그 레이아웃 | `app/src/main/res/layout/activity_today_jelly.xml` | combinedJellyImageView top constraint 추가, 디버그 TextView 3개 추가 |
| Step 전환 로직 | `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java` | 멤버 변수 추가, 감정 이름 저장, showStep2/showStep1 수정 |

---

## Task 1: combinedJellyImageView에 top constraint 추가

**Files:**
- Modify: `app/src/main/res/layout/activity_today_jelly.xml`

- [ ] **Step 1: `combinedJellyImageView`에 top constraint와 marginTop 추가**

`activity_today_jelly.xml`에서 `combinedJellyImageView` 블록 전체를 아래 내용으로 교체한다:

```xml
<ImageView
    android:id="@+id/combinedJellyImageView"
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:layout_marginTop="32dp"
    android:layout_marginBottom="32dp"
    android:contentDescription="@string/cd_jelly_preview"
    android:scaleType="fitCenter"
    android:visibility="gone"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintBottom_toTopOf="@id/emoRecyclerView"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    tools:src="@android:drawable/ic_menu_report_image" />
```

> `constraintTop_toTopOf="parent"`가 없으면 `emoRecyclerView`가 GONE될 때 이미지 위치가 무너진다.

- [ ] **Step 2: 컴파일 확인**

```powershell
.\gradlew.bat compileDebugSources
```

Expected: `BUILD SUCCESSFUL`

---

## Task 2: DEBUG 전용 TextView 3개 추가

**Files:**
- Modify: `app/src/main/res/layout/activity_today_jelly.xml`

- [ ] **Step 1: `combinedJellyImageView` 닫는 태그 바로 뒤에 디버그 TextView 3개 삽입**

```xml
<TextView
    android:id="@+id/tvDebugEmo1"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="8dp"
    android:textSize="12sp"
    android:textColor="#888888"
    android:visibility="gone"
    app:layout_constraintTop_toBottomOf="@id/combinedJellyImageView"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    tools:text="감정1: 기쁨" />

<TextView
    android:id="@+id/tvDebugEmo2"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="4dp"
    android:textSize="12sp"
    android:textColor="#888888"
    android:visibility="gone"
    app:layout_constraintTop_toBottomOf="@id/tvDebugEmo1"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    tools:text="감정2: 설렘" />

<TextView
    android:id="@+id/tvDebugCombId"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="4dp"
    android:textSize="12sp"
    android:textColor="#888888"
    android:visibility="gone"
    app:layout_constraintTop_toBottomOf="@id/tvDebugEmo2"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    tools:text="CombId: 42" />
```

- [ ] **Step 2: 컴파일 확인**

```powershell
.\gradlew.bat compileDebugSources
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: 커밋**

```powershell
git add app/src/main/res/layout/activity_today_jelly.xml
git commit -m "feat: add top constraint and debug TextViews to today_jelly layout"
```

---

## Task 3: 선택된 감정 이름 멤버 변수 추가 및 저장

**Files:**
- Modify: `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java`

- [ ] **Step 1: import 추가**

`TodayJellyActivity.java` 상단 import 블록에 추가:

```java
import com.mindJellyProject.mindjelly.BuildConfig;
```

- [ ] **Step 2: 멤버 변수 추가**

`private boolean isStep2 = false;` 바로 아래에 추가:

```java
private String selectedEmo1Name = "";
private String selectedEmo2Name = "";
```

- [ ] **Step 3: `setOnSelectionChangedListener`에서 감정 이름 저장**

`setupRecyclerView()`의 `setOnSelectionChangedListener` 내부, `if (selectedEmos.size() == 2)` 블록에서 `Long id2 = ...` 다음 줄에 추가:

```java
selectedEmo1Name = selectedEmos.get(0).getEmoName();
selectedEmo2Name = selectedEmos.get(1).getEmoName();
```

`else` 블록(선택 해제 시)에서 `cachedJellyCombId = null;` 다음 줄에 추가:

```java
selectedEmo1Name = "";
selectedEmo2Name = "";
```

- [ ] **Step 4: 컴파일 확인**

```powershell
.\gradlew.bat compileDebugSources
```

Expected: `BUILD SUCCESSFUL`

---

## Task 4: `showStep2()` 수정 — 이미지 유지 + 디버그 데이터 바인딩

**Files:**
- Modify: `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java`

- [ ] **Step 1: `showStep2()` 전체 교체**

기존 `showStep2()` 메서드를 아래 코드로 교체한다:

```java
private void showStep2() {
    isStep2 = true;
    binding.emoRecyclerView.setVisibility(View.GONE);
    binding.combinedJellyImageView.setVisibility(View.VISIBLE);
    binding.btnNext.setVisibility(View.GONE);
    binding.etDiary.setVisibility(View.VISIBLE);
    binding.btnSave.setVisibility(View.VISIBLE);

    if (BuildConfig.DEBUG) {
        binding.tvDebugEmo1.setText("감정1: " + selectedEmo1Name);
        binding.tvDebugEmo2.setText("감정2: " + selectedEmo2Name);
        binding.tvDebugCombId.setText("CombId: " + cachedJellyCombId);
        binding.tvDebugEmo1.setVisibility(View.VISIBLE);
        binding.tvDebugEmo2.setVisibility(View.VISIBLE);
        binding.tvDebugCombId.setVisibility(View.VISIBLE);
    }
}
```

- [ ] **Step 2: 컴파일 확인**

```powershell
.\gradlew.bat compileDebugSources
```

Expected: `BUILD SUCCESSFUL`

---

## Task 5: `showStep1()` 수정 — 디버그 뷰 초기화

**Files:**
- Modify: `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java`

- [ ] **Step 1: `showStep1()` 전체 교체**

기존 `showStep1()` 메서드를 아래 코드로 교체한다:

```java
private void showStep1() {
    isStep2 = false;
    binding.emoRecyclerView.setVisibility(View.VISIBLE);
    binding.btnNext.setVisibility(View.VISIBLE);
    binding.etDiary.setVisibility(View.GONE);
    binding.btnSave.setVisibility(View.GONE);
    binding.tvDebugEmo1.setVisibility(View.GONE);
    binding.tvDebugEmo2.setVisibility(View.GONE);
    binding.tvDebugCombId.setVisibility(View.GONE);
    if (cachedJellyCombId != null) {
        binding.combinedJellyImageView.setVisibility(View.VISIBLE);
        binding.btnNext.setEnabled(true);
        binding.btnNext.setAlpha(1.0f);
    }
}
```

- [ ] **Step 2: 컴파일 확인**

```powershell
.\gradlew.bat compileDebugSources
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: 커밋**

```powershell
git add app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java
git commit -m "feat: show jelly combo info in Step 2 debug view"
```

---

## Task 6: 수동 검증

**Files:**
- 없음 (코드 변경 없음)

- [ ] **Step 1: DEBUG 빌드로 앱 실행**

에뮬레이터 또는 기기에 DEBUG 빌드 설치:

```powershell
.\gradlew.bat installDebug
```

- [ ] **Step 2: 다음 흐름을 순서대로 확인**

```
1. TodayJellyActivity 진입
2. 감정 2개 선택 → 상단에 조합 이미지 나타남
3. "다음" 버튼 탭
4. Step 2 화면에서 확인:
   - 조합 이미지가 상단에 보임 (사라지지 않음)
   - "감정1: [선택한 감정명]" 텍스트 보임
   - "감정2: [선택한 감정명]" 텍스트 보임
   - "CombId: [숫자]" 텍스트 보임
5. 뒤로가기 → Step 1으로 돌아가면 디버그 텍스트 사라짐
6. 다시 "다음" 탭 → 디버그 텍스트 다시 표시됨
```

Expected: 위 6단계 모두 통과.

---

## Self-Review

**스펙 커버리지:**
- [x] Step 2에서 조합 이미지 유지 → Task 4 `showStep2()`
- [x] 감정1 이름 표시 (`getEmoName()`) → Task 3, Task 4
- [x] 감정2 이름 표시 (`getEmoName()`) → Task 3, Task 4
- [x] jellyCombId 표시 → Task 4
- [x] `BuildConfig.DEBUG` 조건부 표시 → Task 4
- [x] Step 1 복귀 시 디버그 뷰 숨김 → Task 5
- [x] 레이아웃 top constraint 추가 → Task 1

**플레이스홀더 없음:** 모든 단계에 실제 코드 포함.

**타입 일관성:** `binding.tvDebugEmo1/2/3` ID가 Task 2(XML)와 Task 4/5(Java) 양쪽에서 동일하게 사용됨.
