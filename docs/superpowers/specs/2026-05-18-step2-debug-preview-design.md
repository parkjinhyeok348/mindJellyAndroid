# Step 2 Debug Preview Design

**Date:** 2026-05-18
**Scope:** `TodayJellyActivity` — Step 2(일기 입력 단계)에서 선택된 젤리 조합 정보를 시각적으로 확인하는 디버그 UI

---

## 목표

Step 1에서 선택한 두 감정과 조합 젤리 정보(이미지, 감정 이름, jellyCombId)가 Step 2로 올바르게 전달되는지 눈으로 확인한다.

---

## 현재 상태

`showStep2()`에서 `combinedJellyImageView`를 `GONE` 처리하기 때문에, Step 2 화면에서는 어떤 젤리가 선택됐는지 전혀 알 수 없다. `cachedJellyCombId`, 두 감정 이름 모두 Logcat 외에는 확인 수단이 없다.

---

## 설계

### 레이아웃 (`activity_today_jelly.xml`)

1. `combinedJellyImageView`에 `app:layout_constraintTop_toTopOf="parent"` 추가.
   - `emoRecyclerView`가 GONE이 되는 Step 2에서도 이미지가 화면 상단에 anchor된다.

2. `combinedJellyImageView` 아래에 DEBUG 전용 `TextView` 3개 추가:

   | id | 내용 |
   |----|------|
   | `tvDebugEmo1` | 감정1 이름 (예: "기쁨") |
   | `tvDebugEmo2` | 감정2 이름 (예: "설렘") |
   | `tvDebugCombId` | jellyCombId 숫자 (예: "CombId: 42") |

   - 초기 visibility: `gone`
   - `BuildConfig.DEBUG` 빌드에서만 `VISIBLE`로 전환
   - 스타일: 작은 텍스트(12sp), 회색 계열, 화면 방해 최소화

### Java (`TodayJellyActivity.java`)

**멤버 변수 추가:**
```java
private String selectedEmo1Name = "";
private String selectedEmo2Name = "";
```

**`setOnSelectionChangedListener` 수정:**
- 두 감정 선택 시 `selectedEmo1Name`, `selectedEmo2Name`에 이름 저장
- 선택 해제(2개 미만) 시 두 변수 모두 빈 문자열로 초기화

**`showStep2()` 수정:**
- `combinedJellyImageView` → `GONE` 대신 `VISIBLE` 유지
- `BuildConfig.DEBUG`이면:
  - `tvDebugEmo1`, `tvDebugEmo2` 텍스트 설정 후 `VISIBLE`
  - `tvDebugCombId`에 `"CombId: " + cachedJellyCombId` 설정 후 `VISIBLE`

**`showStep1()` 수정:**
- 디버그 텍스트뷰 3개 `GONE` 처리

---

## 데이터 흐름

```
Step 1 감정 선택
  └─ selectedEmo1Name / selectedEmo2Name 저장
  └─ cachedJellyCombId 캐싱

btnNext 클릭 → showStep2()
  └─ combinedJellyImageView VISIBLE 유지
  └─ [DEBUG] tvDebugEmo1 = selectedEmo1Name
  └─ [DEBUG] tvDebugEmo2 = selectedEmo2Name
  └─ [DEBUG] tvDebugCombId = "CombId: {cachedJellyCombId}"
```

---

## 범위 제한

- 이 변경은 `TodayJellyActivity`와 `activity_today_jelly.xml`만 수정한다.
- 프로덕션 빌드(`BuildConfig.DEBUG == false`)에서는 디버그 텍스트뷰가 보이지 않는다.
- 조합 이미지는 Step 2에서도 항상 보인다 (DEBUG/프로덕션 모두).
- `BasicEmoResDTO.getEmoName()`으로 감정 이름을 읽는다 — 다른 모델 변경 없음.
