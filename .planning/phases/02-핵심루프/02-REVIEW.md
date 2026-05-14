---
phase: 02-핵심루프
reviewed: 2026-05-14T07:00:00Z
depth: standard
files_reviewed: 21
files_reviewed_list:
  - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/BasicEmoAdapter.java
  - app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java
  - app/src/main/java/com/mindJellyProject/mindjelly/common/ErrorMessageUtil.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyDrawerResDTO.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyStartAgingReqDTO.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyService.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerAdapter.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/viewmodel/JellyViewModel.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombRepository.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombService.java
  - app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/viewmodel/JellyCombViewModel.java
  - app/src/main/res/drawable/bg_edit_text.xml
  - app/src/main/res/drawable/bg_status_badge.xml
  - app/src/main/res/layout/activity_jelly_drawer.xml
  - app/src/main/res/layout/activity_today_jelly.xml
  - app/src/main/res/layout/item_basic_emo.xml
  - app/src/main/res/layout/item_jelly_drawer.xml
  - app/src/main/res/values/colors.xml
  - app/src/main/res/values/strings.xml
  - app/src/test/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyDrawerResDTOTest.java
findings:
  critical: 5
  warning: 6
  info: 3
  total: 14
status: issues_found
---

# Phase 02: Code Review Report

**Reviewed:** 2026-05-14T07:00:00Z
**Depth:** standard
**Files Reviewed:** 21
**Status:** issues_found

## Summary

Phase 2 implements the core emotion-selection and jelly-save loop, the jelly drawer list with aging state management, and supporting infrastructure (ErrorMessageUtil, ViewBinding migration, status badge). The architecture is generally sound — MVVM pattern is followed, ViewBinding is used correctly, and the DiffUtil-based adapter is a good choice.

However, five blockers were found that will cause crashes or incorrect behavior in production: a LiveData accumulation leak in `TodayJellyActivity` that adds a new observer on every save-button press, a NullPointerException on every drawer list item due to an unchecked `getJellyId()` call in `DiffUtil`, a NullPointerException risk from `t.getMessage()` returning null in several repository callbacks, an unconfirmed backend endpoint embedded in shipped code (with an explicit `TODO` comment), and an HTTP base URL mismatch between the service layer and the hardcoded `serverUrl` constants in three files. Six warnings address edge-case correctness problems. Three info items flag code-quality concerns.

---

## Critical Issues

### CR-01: LiveData observer accumulates on every button press (memory leak + duplicate callbacks)

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java:117`

**Issue:** `jellyViewModel.createJelly(reqDTO).observe(this, ...)` is called inside the `btnSave` click listener. Every click registers a **new** observer against the Activity's lifecycle owner. Observers are never removed until the Activity is destroyed, so after N button presses there are N active observers. On a successful save the success Toast fires N times, `finish()` is called N times (only the first succeeds, but N `startActivity` calls are issued), and `jellyViewModel.isLoading.postValue(false)` runs N times. The same pattern appears at line 146 (`jellyCombViewModel.getJellyCombId`) and line 166 (`fetchCombinedJellyIcon`), which accumulate observers every time 2 emotions are selected and de-selected/re-selected.

**Fix:** Move the LiveData observation outside the click listener. Cache the LiveData reference and use `removeObservers` / `MediatorLiveData`, or — the cleanest pattern here — expose a `SingleLiveEvent` or `MutableSharedFlow` from the ViewModel and observe it once in `onCreate`.

A minimal fix for the save path:
```java
// In onCreate, observe once:
jellyViewModel.saveResult.observe(this, resource -> {
    if (resource != null && resource.isSuccess()) {
        Toast.makeText(this, getString(R.string.success_jelly_saved), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, JellyDrawerActivity.class));
        finish();
    } else if (resource != null && resource.isError()) {
        jellyViewModel.isLoading.postValue(false);
        Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
    }
});

// In click listener, just trigger:
binding.btnSave.setOnClickListener(v -> {
    // ... validation ...
    jellyViewModel.createJelly(reqDTO);   // ViewModel posts to saveResult
});
```

The same pattern must be applied to `getJellyCombId` (line 146) and `getJellyIcon` (line 166).

---

### CR-02: NullPointerException in DiffUtil — `getJellyId()` called without null check

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerAdapter.java:27`

**Issue:** `areItemsTheSame` calls `a.getJellyId().equals(b.getJellyId())`. `getJellyId()` returns `Long` (boxed), which will be `null` if the server omits the field or Gson fails to deserialize it. `null.equals(...)` throws `NullPointerException`, crashing the adapter every time `submitList` is called. The same risk exists on line 32 (`areContentsTheSame`).

**Fix:**
```java
@Override
public boolean areItemsTheSame(@NonNull JellyDrawerResDTO a, @NonNull JellyDrawerResDTO b) {
    return Objects.equals(a.getJellyId(), b.getJellyId());
}

@Override
public boolean areContentsTheSame(@NonNull JellyDrawerResDTO a, @NonNull JellyDrawerResDTO b) {
    return Objects.equals(a.getJellyId(), b.getJellyId())
            && Objects.equals(a.getStatus(), b.getStatus())
            && Objects.equals(a.getCreateDate(), b.getCreateDate());
}
```

---

### CR-03: `t.getMessage()` can return null, producing `Resource.error(null)` which propagates as a no-op

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyRepository.java:54` (and lines 75, 96, 117, 138, 158); same pattern in `JellyCombRepository.java:48,70,93`

**Issue:** `Throwable.getMessage()` is documented to return `null` when the exception has no detail message (e.g., `SocketException` with no message, some OkHttp cancellations). When `null` is passed to `Resource.error(null)`, `ErrorMessageUtil.getKoreanMessage` receives a null `errorMsg`, hits the `errorMsg == null` branch, and returns `error_network` — which is technically a coincidental fallback. More critically, `JellyCombRepository.onFailure` wraps the value: `Resource.error("Error: " + t.getMessage())` which produces `"Error: null"` — a string that will be shown to the user verbatim if `ErrorMessageUtil` is not used at that call site (e.g., `TodayJellyActivity:151` logs `resource.getError()` directly).

**Fix:**
```java
@Override
public void onFailure(Call<Jelly> call, Throwable t) {
    String msg = t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();
    liveData.postValue(Resource.error(msg));
}
```
Apply to all `onFailure` callbacks in both repository classes (8 sites).

---

### CR-04: Unconfirmed backend endpoint shipped in production code path

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombService.java:36-42`

**Issue:** The `getJellyCombId` endpoint (`GET /jellyComb/jelly-comb-id/{firstEmo}/{secondEmo}`) carries an explicit `TODO` comment stating the backend API existence is unconfirmed. This endpoint is on the **critical path** for jelly saving: `TodayJellyActivity` caches `jellyCombId` from this call, and if it returns 404/error, `cachedJellyCombId` stays `null`, blocking every save attempt (the user sees `error_generic`). Shipping code with a `TODO: 실제 호출 전 백엔드 API 존재 여부 확인 필요` comment in a production interface is a BLOCKER — it means the feature is gated on an API that may not exist.

**Fix:** Confirm the endpoint exists in the backend before release. If it does not exist, implement the fallback strategy mentioned in the TODO comment (embed `jellyCombId` in the `getJellyIcon` response, or add it server-side). Remove the TODO once resolved.

---

### CR-05: HTTP endpoint URL inconsistency — `startAging` uses `/api/jelly/{jellyId}` while all other endpoints omit the `/api` prefix

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyService.java:57`

**Issue:** Every other endpoint in `JellyService` uses paths without an `/api` prefix (`/jelly`, `/jelly/{jellyId}/info`, `jelly/user/{userId}`). The new `startAging` endpoint uses `@PATCH("/api/jelly/{jellyId}")`. Combined with the base URL `http://10.0.2.2:8080/` in `RetrofitClient`, the effective URL for `startAging` becomes `http://10.0.2.2:8080/api/jelly/{id}` while `createJelly` hits `http://10.0.2.2:8080/jelly`. If the backend does not route `/api/jelly/...`, every aging request returns 404 and the user is redirected to `AgingRoomActivity` only on success — meaning aging silently never works. This is a behavioral bug, not a style issue.

**Fix:** Align the path with the actual backend route. If the backend path is `/api/jelly/{jellyId}`, apply the same prefix to all other endpoints consistently. If the correct path is `/jelly/{jellyId}` (matching the other methods), change line 57:
```java
@PATCH("/jelly/{jellyId}")
Call<ResponseBody> startAging(@Path("jellyId") Long jellyId, @Body JellyStartAgingReqDTO reqDTO);
```

---

## Warnings

### WR-01: Hardcoded emulator base URL in three production adapter/activity files

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/BasicEmoAdapter.java:24`, `TodayJellyActivity.java:45`, `JellyDrawerAdapter.java:38`

**Issue:** `private final String serverUrl = "http://10.0.2.2:8080"` is the Android emulator loopback address. This is duplicated in three places and will fail on any physical device or production server. `RetrofitClient` already centralises the base URL — these constants are stale copies and diverge from it as a single source of truth.

**Fix:** Remove the three `serverUrl` fields and derive the image base URL from the same constant used by `RetrofitClient`, or pass it as a constructor parameter from the ViewModel/Activity where it is already validated.

---

### WR-02: RecyclerView position captured in lambda is stale after item moves

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/BasicEmoAdapter.java:72-89`

**Issue:** The click listener lambda in `onBindViewHolder` captures `position` (the parameter at bind time). If items are inserted or removed before the user clicks, the captured `position` is stale. `selectedPositions.contains(position)` and `selectedPositions.remove(Integer.valueOf(position))` will operate on the wrong index. The standard fix for `RecyclerView.Adapter` (non-`ListAdapter`) is to use `holder.getAdapterPosition()` or `holder.getBindingAdapterPosition()` inside the click listener.

**Fix:**
```java
holder.itemView.setOnClickListener(v -> {
    int adapterPos = holder.getBindingAdapterPosition();
    if (adapterPos == RecyclerView.NO_ID) return; // item removed during animation
    if (selectedPositions.contains(adapterPos)) {
        selectedPositions.remove(Integer.valueOf(adapterPos));
        notifyItemChanged(adapterPos);
    } else {
        if (selectedPositions.size() < 2) {
            selectedPositions.add(adapterPos);
            notifyItemChanged(adapterPos);
        }
    }
    if (selectionChangedListener != null) {
        selectionChangedListener.onSelectionChanged(getSelectedEmos());
    }
});
```

---

### WR-03: `isLoading.postValue(false)` called unconditionally before checking resource state in `JellyDrawerActivity`

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerActivity.java:80`

**Issue:** Inside the `startAging` observer, `jellyViewModel.isLoading.postValue(false)` is called as the first line regardless of whether `resource` is null or non-null. If `resource` is null (which can happen before the first emission), the loading indicator is prematurely hidden. This also means the loading state is reset before the result is known, so the ProgressBar briefly flickers off on every intermediate LiveData emission.

Additionally, `startAging` in `JellyViewModel` calls `isLoading.postValue(true)` (line 81), but no corresponding `postValue(false)` is guaranteed if the Activity is paused or the observer is re-registered. The loading flag can get stuck at `true`.

**Fix:** Reset `isLoading` only after a non-null terminal state:
```java
jellyViewModel.startAging(jellyId).observe(this, resource -> {
    if (resource == null) return;
    jellyViewModel.isLoading.postValue(false);
    if (resource.isSuccess()) { ... }
    else if (resource.isError()) { ... }
});
```

---

### WR-04: `ErrorMessageUtil.isServerError` uses a string-contains loop instead of regex — false positives on data containing 5xx digit sequences

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/common/ErrorMessageUtil.java:91-98`

**Issue:** The method checks whether the error message string contains any substring `"500"` through `"599"`. A user-visible message like `"Error: connection reset after 5001 ms"` or a Retrofit message containing `"5009"` will match `"500"` and return `true`, triggering the `error_server` user message for what is actually a timeout. The pre-check `if (msg.contains("5"))` is also redundant when every iteration of the loop already calls `msg.contains(...)`.

**Fix:** Use a word-boundary-aware check. At minimum, require the status code to appear as a standalone token:
```java
private static boolean isServerError(String msg) {
    // Match " 5xx" or "5xx " — avoid false positives in longer numbers
    return msg.matches(".*\\b5[0-9]{2}\\b.*");
}
```

---

### WR-05: `JellyDrawerAdapter.onBindViewHolder` — `getEmo1Icon()` / `getEmo2Icon()` can be null, producing a malformed Glide URL

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/view/JellyDrawerAdapter.java:70,77`

**Issue:** `serverUrl + jelly.getEmo1Icon()` evaluates to `"http://10.0.2.2:8080null"` when the field is absent from the JSON response (Gson leaves it as `null`). Glide will attempt to load the literal string `"...null"`, fail with an HTTP error, and show the error drawable — but only after an unnecessary network round-trip. For `getEmo2Icon()` the same applies. This is distinct from the Glide error drawable being shown; the malformed URL wastes a network call and logs a confusing error.

**Fix:**
```java
String emo1Icon = jelly.getEmo1Icon();
if (emo1Icon != null) {
    Glide.with(holder.itemView.getContext())
        .load(serverUrl + emo1Icon)
        ...
        .into(holder.emo1ImageView);
} else {
    holder.emo1ImageView.setImageResource(android.R.drawable.ic_menu_report_image);
}
```
Apply the same guard for `emo2Icon` and for `BasicEmoAdapter` line 58 (`emo.getEmoIcon()` may also be null).

---

### WR-06: `JellyDrawerResDTO` constructor does not initialise Phase 2 fields — partial construction silently leaves emo/status null

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/model/JellyDrawerResDTO.java:38-43`

**Issue:** The only constructor sets `jellyId`, `jellyCombId`, `isAging`, and `createDate`. The five Phase 2 fields (`emo1Name`, `emo1Icon`, `emo2Name`, `emo2Icon`, `status`) are not included. Any code that constructs `JellyDrawerResDTO` directly (as done in the unit test, and potentially in any mock/fake data path) will get an object where `getStatus()` returns null. In `JellyDrawerAdapter`, the null status hits the `else` branch (WAITING fallback), which is acceptable at runtime, but the incomplete constructor makes it easy for test authors to believe the object is fully initialised. At minimum, add a full-args constructor or a builder, or document the partial-construction intent explicitly.

**Fix:** Add a complete constructor or use the builder pattern:
```java
public JellyDrawerResDTO(Long jellyId, Long jellyCombId, Boolean isAging, String createDate,
                          String emo1Name, String emo1Icon, String emo2Name, String emo2Icon, String status) {
    this.jellyId = jellyId;
    this.jellyCombId = jellyCombId;
    this.isAging = isAging;
    this.createDate = createDate;
    this.emo1Name = emo1Name;
    this.emo1Icon = emo1Icon;
    this.emo2Name = emo2Name;
    this.emo2Icon = emo2Icon;
    this.status = status;
}
```

---

## Info

### IN-01: `JellyCombViewModel` import statements are out of order / placed mid-file

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/viewmodel/JellyCombViewModel.java:6-24`

**Issue:** The class declaration block begins at line 6 but import statements appear at lines 18-23, after the opening of the class body. This is unusual Java formatting and some tools (e.g., certain IDEs / lint rules) will flag it as a structural anomaly. It compiles because `package` and `import` statements are processed at the file level, but it is misleading to readers and violates standard Java file structure.

**Fix:** Move all import statements to lines 3-6, above the class Javadoc and class declaration.

---

### IN-02: Magic string `"7"` used as a hardcoded field value in `JellySaveReqDTO` construction

**File:** `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java:112`

**Issue:** `new JellySaveReqDTO(userId, cachedJellyCombId, "오늘의 젤리", diary, "7", today, null)` — the fifth argument `"7"` is unexplained. It is neither documented nor traced to a constant. If this represents a category, aging duration, or some other domain concept, it must be named.

**Fix:** Define a named constant:
```java
private static final String DEFAULT_JELLY_AGING_DAYS = "7";
// or use an enum/int constant matching the domain model
```

---

### IN-03: `bg_status_badge.xml` default colour is yellow (waiting) — overridden at runtime but initial render flashes yellow for AGING/MATURED items

**File:** `app/src/main/res/drawable/bg_status_badge.xml:4`

**Issue:** The `bg_status_badge` drawable hard-codes `#FFFFEEBA` (yellow/waiting colour) as its `solid` colour. In `JellyDrawerAdapter.onBindViewHolder`, the background colour is overridden via `GradientDrawable.setColor()` at bind time. For AGING and MATURED items there is a brief flash of the default yellow before the correct colour is applied, especially visible during fast scroll. This is a cosmetic issue but can look like a rendering bug.

**Fix:** Change the default `solid` colour in `bg_status_badge.xml` to transparent or a neutral colour, and rely entirely on the programmatic colour set in the adapter:
```xml
<solid android:color="@android:color/transparent" />
```

---

_Reviewed: 2026-05-14T07:00:00Z_
_Reviewer: Claude (gsd-code-reviewer)_
_Depth: standard_
