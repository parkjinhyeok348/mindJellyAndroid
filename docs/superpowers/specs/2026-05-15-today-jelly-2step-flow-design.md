# Design: TodayJelly 2단계 흐름 + 로그인 바이패스

**Date:** 2026-05-15
**Phase:** 02-핵심루프 (테스트 편의 개선)
**Scope:** SplashActivity 1줄 + TodayJellyActivity XML + Java

---

## Goal

개발 테스트 시 로그인 없이 MainActivity에서 시작하고, 감정 선택 → "다음" → 일기 작성 → 저장 흐름을 2단계로 분리한다.

---

## 1. 로그인 바이패스

**파일:** `SplashActivity.java`

**변경:** `hasToken()` 체크에 `BuildConfig.DEBUG` 조건 추가

```java
// Before
if (sessionManager.hasToken()) {

// After
if (BuildConfig.DEBUG || sessionManager.hasToken()) {
```

**제약:**
- `BuildConfig.DEBUG`는 release 빌드에서 자동 `false` → 프로덕션 영향 없음
- 에뮬레이터에 저장된 세션 토큰이 있으면 API 정상 동작
- 세션 없으면 API 401 (예상 동작, 테스트 범위 밖)

---

## 2. TodayJellyActivity 2단계 UI

### 2-1. 뷰 상태 정의

| 뷰 | Step 1 (감정 선택) | Step 2 (일기 작성) |
|----|-------------------|-------------------|
| `combinedJellyImageView` | VISIBLE (2개 선택 시) / GONE (미만) | GONE |
| `emoRecyclerView` | VISIBLE | GONE |
| `btnNext` | VISIBLE, 2개 선택 시 enabled | GONE |
| `etDiary` | GONE | VISIBLE |
| `btnSave` | GONE | VISIBLE |
| `pbLoading` | GONE | GONE → 저장 중 VISIBLE |

### 2-2. XML 변경 (`activity_today_jelly.xml`)

- `btnNext` 신규 추가 (ConstraintLayout 하단 고정, `btnSave`와 동일 위치/스타일)
- `etDiary`, `btnSave` 초기 visibility = `gone`
- `btnNext` 초기 visibility = `visible`, `enabled = false`

### 2-3. Java 변경 (`TodayJellyActivity.java`)

**필드 추가:**
```java
private boolean isStep2 = false;
```

**감정 선택 콜백 내 btnNext 활성화:**
```java
// 선택 2개일 때
binding.btnNext.setEnabled(true);
binding.btnNext.setAlpha(1.0f);

// 2개 미만일 때
binding.btnNext.setEnabled(false);
binding.btnNext.setAlpha(0.5f);
```

**btnNext 클릭 리스너:**
```java
binding.btnNext.setOnClickListener(v -> showStep2());
```

**showStep1() / showStep2() 헬퍼:**
```java
private void showStep2() {
    isStep2 = true;
    binding.emoRecyclerView.setVisibility(View.GONE);
    binding.combinedJellyImageView.setVisibility(View.GONE);
    binding.btnNext.setVisibility(View.GONE);
    binding.etDiary.setVisibility(View.VISIBLE);
    binding.btnSave.setVisibility(View.VISIBLE);
}

private void showStep1() {
    isStep2 = false;
    binding.etDiary.setVisibility(View.GONE);
    binding.btnSave.setVisibility(View.GONE);
    binding.emoRecyclerView.setVisibility(View.VISIBLE);
    binding.btnNext.setVisibility(View.VISIBLE);
    // combinedJellyImageView는 선택 상태에 따라 onSelectionChanged에서 관리
}
```

**onBackPressed 오버라이드:**
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

### 2-4. 저장 흐름 (기존 유지)

- `btnSave` 클릭 → `cachedJellyCombId` null 체크 → `userId` 체크 → `createJelly` 호출
- 성공: Toast + `JellyDrawerActivity` 이동 + `finish()`
- 기존 로직 변경 없음

---

## 3. 변경 파일 요약

| 파일 | 변경 내용 |
|------|----------|
| `SplashActivity.java` | `BuildConfig.DEBUG` 조건 1줄 추가 |
| `activity_today_jelly.xml` | `btnNext` 추가, `etDiary`/`btnSave` 초기 GONE |
| `TodayJellyActivity.java` | `isStep2` 필드, `showStep1/2()`, `btnNext` 리스너, `onBackPressed` |

---

## 4. Out of Scope

- Step 전환 애니메이션 (v2 검토)
- Step 1에서 선택 취소 후 Step 2 진입 방지 이외의 유효성 검사 (기존 로직으로 충분)
- 실기기 실서버 테스트 (에뮬레이터 로컬 백엔드 기준)

---

*Designed: 2026-05-15*
