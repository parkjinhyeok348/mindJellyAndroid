# Settings Button Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 메인 화면 우측 상단에 ⚙️ 설정 버튼을 추가하고 클릭 시 기존 SettingActivity로 이동한다.

**Architecture:** 기존 RelativeLayout 안에 ImageButton을 alignParentTop + alignParentEnd로 배치한다. SettingActivity는 이미 존재하고 로그아웃 버튼도 구현되어 있으므로 연결만 한다.

**Tech Stack:** Android (Java), ConstraintLayout/RelativeLayout, Vector Drawable

---

### Task 1: 설정 아이콘 벡터 드로어블 추가

**Files:**
- Create: `app/src/main/res/drawable/ic_settings.xml`

- [ ] **Step 1: `ic_settings.xml` 파일 생성**

`app/src/main/res/drawable/ic_settings.xml`을 아래 내용으로 생성한다:

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M19.14,12.94c0.04,-0.3 0.06,-0.61 0.06,-0.94c0,-0.32 -0.02,-0.64 -0.07,-0.94l2.03,-1.58c0.18,-0.14 0.23,-0.41 0.12,-0.61l-1.92,-3.32c-0.12,-0.22 -0.37,-0.29 -0.59,-0.22l-2.39,0.96c-0.5,-0.38 -1.03,-0.7 -1.62,-0.94L14.4,2.81c-0.04,-0.24 -0.24,-0.41 -0.48,-0.41h-3.84c-0.24,0 -0.43,0.17 -0.47,0.41L9.25,5.35C8.66,5.59 8.12,5.92 7.63,6.29L5.24,5.33c-0.22,-0.08 -0.47,0 -0.59,0.22L2.74,8.87C2.62,9.08 2.66,9.34 2.86,9.48l2.03,1.58C4.84,11.36 4.8,11.69 4.8,12s0.02,0.64 0.07,0.94l-2.03,1.58c-0.18,0.14 -0.23,0.41 -0.12,0.61l1.92,3.32c0.12,0.22 0.37,0.29 0.59,0.22l2.39,-0.96c0.5,0.38 1.03,0.7 1.62,0.94l0.36,2.54c0.05,0.24 0.24,0.41 0.48,0.41h3.84c0.24,0 0.44,-0.17 0.47,-0.41l0.36,-2.54c0.59,-0.24 1.13,-0.56 1.62,-0.94l2.39,0.96c0.22,0.08 0.47,0 0.59,-0.22l1.92,-3.32c0.12,-0.22 0.07,-0.47 -0.12,-0.61L19.14,12.94zM12,15.6c-1.98,0 -3.6,-1.62 -3.6,-3.6s1.62,-3.6 3.6,-3.6s3.6,1.62 3.6,3.6S13.98,15.6 12,15.6z"/>
</vector>
```

- [ ] **Step 2: Android Studio에서 드로어블 확인**

`res/drawable/ic_settings.xml`을 열어 Preview 탭에서 흰색 기어 아이콘이 표시되는지 확인한다.

- [ ] **Step 3: 커밋**

```bash
git add app/src/main/res/drawable/ic_settings.xml
git commit -m "feat: add settings gear icon vector drawable"
```

---

### Task 2: 메인 레이아웃에 설정 버튼 추가

**Files:**
- Modify: `app/src/main/res/layout/activity_main.xml`

- [ ] **Step 1: `activity_main.xml`의 RelativeLayout 안에 ImageButton 추가**

`app/src/main/res/layout/activity_main.xml`의 `</RelativeLayout>` 닫는 태그 바로 앞에 다음을 추가한다:

```xml
        <ImageButton
            android:id="@+id/btn_settings"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:layout_alignParentTop="true"
            android:layout_alignParentEnd="true"
            android:layout_margin="12dp"
            android:src="@drawable/ic_settings"
            android:background="@android:color/transparent"
            android:contentDescription="설정" />
```

- [ ] **Step 2: Layout Preview에서 위치 확인**

Android Studio Layout Editor에서 `activity_main.xml`을 열어 기어 아이콘이 우측 상단에 위치하는지 확인한다.

- [ ] **Step 3: 커밋**

```bash
git add app/src/main/res/layout/activity_main.xml
git commit -m "feat: add settings button to MainActivity layout"
```

---

### Task 3: MainActivity에 설정 버튼 클릭 이벤트 연결

**Files:**
- Modify: `app/src/main/java/com/mindJellyProject/mindjelly/MainActivity.java`

- [ ] **Step 1: import 추가**

`MainActivity.java` 상단의 import 목록에 다음 두 줄을 추가한다:

```java
import android.widget.ImageButton;
import com.mindJellyProject.mindjelly.users.view.SettingActivity;
```

- [ ] **Step 2: onCreate에 버튼 초기화 및 클릭 리스너 추가**

기존 버튼 초기화 블록(`Button btnTodayJelly = ...`) 아래에 다음을 추가한다:

```java
        ImageButton btnSettings = findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingActivity.class)));
```

- [ ] **Step 3: 빌드 오류 없는지 확인**

Android Studio에서 `Build → Make Project` 실행. 오류 없이 빌드 성공해야 한다.

- [ ] **Step 4: 기기/에뮬레이터에서 동작 확인**

앱 실행 후:
1. 메인 화면 우측 상단에 ⚙️ 아이콘이 보이는지 확인
2. 아이콘 클릭 시 SettingActivity로 이동하는지 확인
3. SettingActivity에서 로그아웃 버튼 클릭 시 LoginActivity로 이동하고 세션이 삭제되는지 확인

- [ ] **Step 5: 최종 커밋**

```bash
git add app/src/main/java/com/mindJellyProject/mindjelly/MainActivity.java
git commit -m "feat: wire settings button to SettingActivity in MainActivity"
```