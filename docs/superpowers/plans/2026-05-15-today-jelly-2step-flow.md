# TodayJelly 2단계 흐름 + 로그인 바이패스 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 로그인 없이 MainActivity 직행(DEBUG), TodayJellyActivity를 감정 선택 → "다음" → 일기 작성 2단계로 분리한다.

**Architecture:** SplashActivity에 BuildConfig.DEBUG 조건 1줄 추가로 바이패스 구현. TodayJellyActivity는 단일 Activity 유지, XML에 btnNext 추가 + 초기 visibility 조정, Java에서 isStep2 필드로 단계 관리.

**Tech Stack:** Android Native Java, ViewBinding (ActivityTodayJellyBinding), ConstraintLayout

---

## File Map

| 역할 | 파일 | 변경 |
|------|------|------|
| 로그인 바이패스 | `app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java` | Modify — 조건 1줄 |
| 2단계 레이아웃 | `app/src/main/res/layout/activity_today_jelly.xml` | Modify — btnNext 추가, etDiary/btnSave 초기 GONE, emoRecyclerView 제약 변경 |
| 2단계 로직 | `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java` | Modify — isStep2 필드, showStep1/2(), btnNext 리스너, onBackPressed |

---

## Task 1: SplashActivity 로그인 바이패스

**Files:**
- Modify: `app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java`

- [ ] **Step 1: SplashActivity.java 열어서 현재 토큰 체크 라인 확인**

  파일 36번째 줄 근처:
  ```java
  if (sessionManager.hasToken()) {
  ```

- [ ] **Step 2: BuildConfig.DEBUG 조건 추가**

  ```java
  // 변경 전
  if (sessionManager.hasToken()) {

  // 변경 후
  if (BuildConfig.DEBUG || sessionManager.hasToken()) {
  ```

  `BuildConfig`는 이미 빌드 시 생성됨 — import 불필요.

- [ ] **Step 3: 컴파일 검증**

  ```
  .\gradlew.bat compileDebugSources
  ```
  Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: 커밋**

  ```
  git add app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java
  git commit -m "feat: bypass login in DEBUG builds for testing"
  ```

---

## Task 2: activity_today_jelly.xml 2단계 레이아웃

**Files:**
- Modify: `app/src/main/res/layout/activity_today_jelly.xml`

현재 제약 체인:
```
combinedJellyImageView → (bottom) emoRecyclerView → (bottom) etDiary → (bottom) btnSave → parent
```

목표 체인 (두 병렬 체인):
```
[Step1] combinedJellyImageView → emoRecyclerView → btnNext → parent
[Step2] etDiary → btnSave → parent
```

- [ ] **Step 1: emoRecyclerView의 bottom 제약 변경**

  현재:
  ```xml
  app:layout_constraintBottom_toTopOf="@id/etDiary"
  ```
  변경 후:
  ```xml
  app:layout_constraintBottom_toTopOf="@id/btnNext"
  ```

- [ ] **Step 2: etDiary 초기 visibility 추가**

  현재 `etDiary`에 visibility 속성 없음. 추가:
  ```xml
  android:visibility="gone"
  ```

- [ ] **Step 3: btnSave 초기 visibility 추가**

  현재 `btnSave`에 visibility 속성 없음. 추가:
  ```xml
  android:visibility="gone"
  ```

- [ ] **Step 4: btnNext 추가**

  `btnSave` 바로 아래에 추가 (같은 위치 제약, 초기 VISIBLE + 비활성화):
  ```xml
  <Button
      android:id="@+id/btnNext"
      android:layout_width="0dp"
      android:layout_height="48dp"
      android:layout_marginStart="16dp"
      android:layout_marginEnd="16dp"
      android:layout_marginBottom="48dp"
      android:backgroundTint="@color/accent_blue"
      android:enabled="false"
      android:alpha="0.5"
      android:text="다음"
      android:textColor="@color/white"
      android:textSize="14sp"
      app:layout_constraintBottom_toBottomOf="parent"
      app:layout_constraintEnd_toEndOf="parent"
      app:layout_constraintStart_toStartOf="parent" />
  ```

- [ ] **Step 5: 최종 XML 전체 구조 확인**

  완성된 `activity_today_jelly.xml` (전체):
  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <androidx.constraintlayout.widget.ConstraintLayout
      xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:app="http://schemas.android.com/apk/res-auto"
      xmlns:tools="http://schemas.android.com/tools"
      android:id="@+id/main"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:background="@drawable/basic_background"
      tools:context=".basicEmo.view.TodayJellyActivity">

      <ImageView
          android:id="@+id/combinedJellyImageView"
          android:layout_width="100dp"
          android:layout_height="100dp"
          android:layout_marginBottom="32dp"
          android:contentDescription="@string/cd_jelly_preview"
          android:scaleType="fitCenter"
          android:visibility="gone"
          app:layout_constraintBottom_toTopOf="@id/emoRecyclerView"
          app:layout_constraintEnd_toEndOf="parent"
          app:layout_constraintStart_toStartOf="parent"
          tools:src="@android:drawable/ic_menu_report_image" />

      <androidx.recyclerview.widget.RecyclerView
          android:id="@+id/emoRecyclerView"
          android:layout_width="0dp"
          android:layout_height="0dp"
          android:layout_marginStart="16dp"
          android:layout_marginEnd="16dp"
          android:layout_marginBottom="16dp"
          android:fadeScrollbars="false"
          android:scrollbars="vertical"
          app:layout_constraintBottom_toTopOf="@id/btnNext"
          app:layout_constraintEnd_toEndOf="parent"
          app:layout_constraintHeight_percent="0.5"
          app:layout_constraintStart_toStartOf="parent"
          app:layoutManager="androidx.recyclerview.widget.GridLayoutManager"
          app:spanCount="3" />

      <EditText
          android:id="@+id/etDiary"
          android:layout_width="0dp"
          android:layout_height="wrap_content"
          android:layout_marginStart="16dp"
          android:layout_marginEnd="16dp"
          android:layout_marginBottom="12dp"
          android:background="@drawable/bg_edit_text"
          android:gravity="top|start"
          android:hint="@string/hint_diary_entry"
          android:inputType="textMultiLine|textCapSentences"
          android:maxLines="5"
          android:minHeight="80dp"
          android:padding="12dp"
          android:textColor="@color/text_primary"
          android:textColorHint="@color/text_hint"
          android:textSize="14sp"
          android:visibility="gone"
          app:layout_constraintBottom_toTopOf="@id/btnSave"
          app:layout_constraintEnd_toEndOf="parent"
          app:layout_constraintStart_toStartOf="parent" />

      <Button
          android:id="@+id/btnSave"
          android:layout_width="0dp"
          android:layout_height="48dp"
          android:layout_marginStart="16dp"
          android:layout_marginEnd="16dp"
          android:layout_marginBottom="48dp"
          android:backgroundTint="@color/accent_blue"
          android:text="@string/btn_save_jelly"
          android:textColor="@color/white"
          android:textSize="14sp"
          android:visibility="gone"
          app:layout_constraintBottom_toBottomOf="parent"
          app:layout_constraintEnd_toEndOf="parent"
          app:layout_constraintStart_toStartOf="parent" />

      <Button
          android:id="@+id/btnNext"
          android:layout_width="0dp"
          android:layout_height="48dp"
          android:layout_marginStart="16dp"
          android:layout_marginEnd="16dp"
          android:layout_marginBottom="48dp"
          android:backgroundTint="@color/accent_blue"
          android:enabled="false"
          android:alpha="0.5"
          android:text="다음"
          android:textColor="@color/white"
          android:textSize="14sp"
          app:layout_constraintBottom_toBottomOf="parent"
          app:layout_constraintEnd_toEndOf="parent"
          app:layout_constraintStart_toStartOf="parent" />

      <ProgressBar
          android:id="@+id/pbLoading"
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:indeterminate="true"
          android:visibility="gone"
          app:layout_constraintBottom_toBottomOf="parent"
          app:layout_constraintEnd_toEndOf="parent"
          app:layout_constraintStart_toStartOf="parent"
          app:layout_constraintTop_toTopOf="parent" />

  </androidx.constraintlayout.widget.ConstraintLayout>
  ```

- [ ] **Step 6: 컴파일 검증**

  ```
  .\gradlew.bat compileDebugSources
  ```
  Expected: `BUILD SUCCESSFUL`

- [ ] **Step 7: 수용 기준 grep 확인**

  ```powershell
  Select-String -Path "app\src\main\res\layout\activity_today_jelly.xml" -Pattern "btnNext"
  Select-String -Path "app\src\main\res\layout\activity_today_jelly.xml" -Pattern 'visibility="gone"'
  Select-String -Path "app\src\main\res\layout\activity_today_jelly.xml" -Pattern 'toTopOf="@id/btnNext"'
  ```
  Expected: 각각 1건 이상 매칭

- [ ] **Step 8: 커밋**

  ```
  git add app/src/main/res/layout/activity_today_jelly.xml
  git commit -m "feat: add btnNext and 2-step visibility to today_jelly layout"
  ```

---

## Task 3: TodayJellyActivity.java 2단계 로직

**Files:**
- Modify: `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java`

- [ ] **Step 1: isStep2 필드 추가**

  클래스 필드 블록 (기존 `cachedJellyCombId` 아래)에 추가:
  ```java
  private boolean isStep2 = false;
  ```

- [ ] **Step 2: showStep1() 헬퍼 메서드 추가**

  클래스 마지막 메서드 아래 추가:
  ```java
  private void showStep1() {
      isStep2 = false;
      binding.emoRecyclerView.setVisibility(View.VISIBLE);
      binding.btnNext.setVisibility(View.VISIBLE);
      binding.etDiary.setVisibility(View.GONE);
      binding.btnSave.setVisibility(View.GONE);
      // combinedJellyImageView는 선택 상태에 따라 onSelectionChangedListener가 관리
  }
  ```

- [ ] **Step 3: showStep2() 헬퍼 메서드 추가**

  `showStep1()` 아래 추가:
  ```java
  private void showStep2() {
      isStep2 = true;
      binding.emoRecyclerView.setVisibility(View.GONE);
      binding.combinedJellyImageView.setVisibility(View.GONE);
      binding.btnNext.setVisibility(View.GONE);
      binding.etDiary.setVisibility(View.VISIBLE);
      binding.btnSave.setVisibility(View.VISIBLE);
  }
  ```

- [ ] **Step 4: onCreate에 btnNext 클릭 리스너 추가**

  `onCreate`에서 `jellyViewModel.isLoading.observe(...)` 블록 아래에 추가:
  ```java
  // 다음 버튼 — Step 1 → Step 2 전환
  binding.btnNext.setOnClickListener(v -> showStep2());
  ```

- [ ] **Step 5: setupRecyclerView()의 선택 콜백에 btnNext 활성화 로직 추가**

  기존 `onSelectionChangedListener` 콜백 안, `if (selectedEmos.size() == 2)` 블록 **맨 앞**에 추가:
  ```java
  binding.btnNext.setEnabled(true);
  binding.btnNext.setAlpha(1.0f);
  ```

  `else` 블록 **맨 앞**에 추가:
  ```java
  binding.btnNext.setEnabled(false);
  binding.btnNext.setAlpha(0.5f);
  ```

  변경 후 전체 콜백:
  ```java
  adapter.setOnSelectionChangedListener(selectedEmos -> {
      if (selectedEmos.size() == 2) {
          binding.btnNext.setEnabled(true);
          binding.btnNext.setAlpha(1.0f);

          Long id1 = selectedEmos.get(0).getEmoId();
          Long id2 = selectedEmos.get(1).getEmoId();

          fetchCombinedJellyIcon(id1, id2);

          jellyCombViewModel.getJellyCombId(id1, id2).observe(this, resource -> {
              if (resource != null && resource.isSuccess()) {
                  cachedJellyCombId = resource.getData();
                  Log.d(TAG, "jellyCombId 캐싱 완료: " + cachedJellyCombId);
              } else if (resource != null && resource.isError()) {
                  Log.e(TAG, "jellyCombId 조회 실패: " + resource.getError());
                  cachedJellyCombId = null;
              }
          });
      } else {
          binding.btnNext.setEnabled(false);
          binding.btnNext.setAlpha(0.5f);

          binding.combinedJellyImageView.setVisibility(View.GONE);
          binding.combinedJellyImageView.setImageDrawable(null);
          cachedJellyCombId = null;
      }
  });
  ```

- [ ] **Step 6: onBackPressed 오버라이드 추가**

  `showStep2()` 아래 추가:
  ```java
  @Override
  public void onBackPressed() {
      if (isStep2) {
          showStep1();
      } else {
          super.onBackPressed();
      }
  }
  ```

- [ ] **Step 7: 컴파일 검증**

  ```
  .\gradlew.bat compileDebugSources
  ```
  Expected: `BUILD SUCCESSFUL`

- [ ] **Step 8: 수용 기준 grep 확인**

  ```powershell
  Select-String -Path "app\src\main\java\com\mindJellyProject\mindjelly\basicEmo\view\TodayJellyActivity.java" -Pattern "isStep2"
  Select-String -Path "app\src\main\java\com\mindJellyProject\mindjelly\basicEmo\view\TodayJellyActivity.java" -Pattern "showStep1|showStep2"
  Select-String -Path "app\src\main\java\com\mindJellyProject\mindjelly\basicEmo\view\TodayJellyActivity.java" -Pattern "btnNext"
  Select-String -Path "app\src\main\java\com\mindJellyProject\mindjelly\basicEmo\view\TodayJellyActivity.java" -Pattern "onBackPressed"
  ```
  Expected: 각각 1건 이상 매칭

- [ ] **Step 9: 커밋**

  ```
  git add app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java
  git commit -m "feat: implement 2-step flow in TodayJellyActivity"
  ```

---

## 수동 검증 체크리스트 (에뮬레이터)

- [ ] 앱 실행 → 로그인 화면 없이 MainActivity 진입
- [ ] "오늘의 마음젤리" 버튼 클릭 → TodayJellyActivity 진입, 감정 그리드 표시
- [ ] 감정 1개 선택 → "다음" 버튼 비활성화(흐릿) 유지
- [ ] 감정 2개 선택 → 합성 프리뷰 이미지 표시 + "다음" 버튼 활성화
- [ ] "다음" 클릭 → 그리드·프리뷰 사라지고 일기 EditText + 저장 버튼 표시
- [ ] Step 2에서 뒤로가기 → 그리드 복귀 (선택 상태 + 프리뷰 유지)
- [ ] Step 1에서 뒤로가기 → TodayJellyActivity 종료, MainActivity 복귀
- [ ] 일기 작성 후 저장 → 성공 Toast + JellyDrawerActivity 이동

---

*Plan written: 2026-05-15*
