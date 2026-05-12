# Coding Conventions

**Analysis Date:** 2026-05-12

## Naming Patterns

**Files:**
- Activity files: PascalCase suffixed with `Activity` — e.g., `LoginActivity.java`, `TodayJellyActivity.java`
- Adapter files: PascalCase suffixed with `Adapter` — e.g., `BasicEmoAdapter.java`, `JellyDrawerAdapter.java`
- ViewModel files: PascalCase suffixed with `ViewModel` — e.g., `AgedEmoViewModel.java`, `UserViewModel.java`
- Repository files: PascalCase suffixed with `Repository` — e.g., `AgedEmoRepository.java`, `JellyRepository.java`
- Service interface files: PascalCase suffixed with `Service` — e.g., `AgedEmoService.java`, `UserService.java`
- Model/entity files: PascalCase noun — e.g., `AgedEmo.java`, `Users.java`
- Request DTOs: `[Entity][Action]ReqDTO` — e.g., `AgedEmoSaveReqDTO.java`, `AgedEmoUpdateReqDTO.java`
- Response DTOs: `[Entity][Purpose]ResDTO` — e.g., `AgedEmoResDTO.java`, `AgedEmoMuseumResDTO.java`
- Known violation: `jellyMuseumActivity.java` starts with lowercase (should be `JellyMuseumActivity.java`)

**Classes:**
- All class names: PascalCase
- Domain entity classes: plain noun — `AgedEmo`, `Jelly`, `Users`
- Request DTOs: `[Entity][Action]ReqDTO`
- Response DTOs: `[Entity][Purpose]ResDTO`

**Methods:**
- camelCase throughout
- Repository methods follow verb-noun pattern: `createAgedEmo()`, `getAgedEmoById()`, `updateAgedEmo()`, `deleteUser()`
- ViewModel methods mirror repository methods 1:1 (thin delegation)
- Java bean getters/setters: `getAgedEmoId()` / `setAgedEmoId()`
- Boolean state methods: `isSuccess()`, `isError()` in `Resource<T>`

**Variables:**
- camelCase for all local and instance variables
- View references prefixed by type abbreviation: `etEmail` (EditText), `btnLogin` (Button), `tvJellyId` (TextView)
- LiveData variables named for content: `agedEmoLiveData`, `resultLiveData`
- Constants: UPPER_SNAKE_CASE — `BASE_URL`, `TAG`
- Immutable fields declared `private final`

**Packages:**
- Base package: `com.mindJellyProject.mindjelly`
- Domain packages: camelCase suffixed with `Domain` — `agedEmoDomain`, `jellyDomain`
- Sub-packages: lowercase noun — `model`, `retrofit`, `view`, `viewmodel`
- Cross-cutting: `common`

## Code Style

**Formatting:**
- No formatter config detected (no `.editorconfig`, Spotless, or Checkstyle)
- 4-space indentation (Android Studio default)
- Opening braces on same line as declaration
- Closing braces on own line

**Linting:**
- No Checkstyle/PMD/SpotBugs config detected
- Android Studio default inspections assumed

## Import Organization

**Order observed:**
1. `android.*` — Android SDK
2. `androidx.*` — AndroidX libraries
3. Third-party (Retrofit, Glide, Gson)
4. Internal project (`com.mindJellyProject.mindjelly.*`)
5. `java.*` — Standard library

No wildcard imports except test files (`import static org.junit.Assert.*`).

**Path aliases:** None — full package paths everywhere.

## Error Handling

**Pattern — Resource wrapper:**

All async API calls return `LiveData<Resource<T>>` where `Resource<T>` is defined in
`app/src/main/java/com/mindJellyProject/mindjelly/common/Resource.java`.

Success path in Repository:
```java
liveData.postValue(Resource.success(response.body()));
```

HTTP error path:
```java
liveData.postValue(Resource.error("Error: " + response.message()));
```

Network failure path:
```java
liveData.postValue(Resource.error(t.getMessage()));
```

UI layer consumption pattern:
```java
viewModel.someMethod(dto).observe(this, resource -> {
    if (resource != null && resource.isSuccess()) {
        // use resource.getData()
    } else if (resource != null && resource.isError()) {
        Toast.makeText(this, resource.getError(), Toast.LENGTH_SHORT).show();
    }
});
```

**Inconsistency:** `AgedEmoRepository` null-checks `response.body()` before posting success;
`JellyRepository` does not. Always include `response.body() != null` guard to avoid NPE.

**User-facing errors:** `Toast.makeText(this, message, Toast.LENGTH_SHORT).show()`

**No try/catch in production code** — all errors route through Retrofit `onFailure` callback.

## Logging

**Framework:** `android.util.Log`

**Patterns:**
- `TAG` constant at class level: `private static final String TAG = "ClassName";`
- `Log.d(TAG, ...)` for debug/request tracing
- `Log.e(TAG, ...)` for error conditions
- Logging used only in Activity/View layer — not in Repository or ViewModel

Example from `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java`:
```java
private static final String TAG = "TodayJellyActivity";
Log.d(TAG, "firstEmo: " + firstEmo + ", secondEmo: " + secondEmo);
Log.e(TAG, "로딩 에러: " + resource.getError());
```

## Comments

**Language:** Korean for business logic; English for technical notes.

**Inline comments:** Korean phrases explaining intent:
```java
// View 초기화
// 로그인 버튼 클릭 이벤트
// 젤리 조합 뷰모델 초기화
```

**Class-level Javadoc header** (used on domain/common layer files, NOT on Activity or Adapter files):
```java
/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.[package]
 * @description : [Korean description]
 * @modification : YYYY-MM-DD(Author) 수정
 * @date : YYYY-MM-DD
 */
```

## Function Design

**Size:**
- Repository methods: 10–20 lines each (one API call per method)
- Activity `onCreate()`: 40–90 lines (all setup and listeners inline)

**Parameters:** Single DTO for create/update; primitive `Long` id for lookups. Max 2 parameters.

**Return values:** Async operations always return `LiveData<Resource<T>>`.

## Module Design

**View Binding:**
- Enabled in `app/build.gradle` (`buildFeatures { viewBinding true }`)
- Preferred: `ActivityXxxBinding.inflate(getLayoutInflater())`, access views via `binding.viewId`
- `TodayJellyActivity` and `JellyDrawerActivity` use ViewBinding correctly
- `LoginActivity` uses `findViewById()` — inconsistent; prefer ViewBinding for all new Activities

**ViewModel instantiation — two patterns exist, use the first:**
```java
// CORRECT: lifecycle-managed (LoginActivity pattern)
userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

// AVOID: manual constructor bypasses lifecycle (TodayJellyActivity pattern)
jellyCombViewModel = new JellyCombViewModel(new JellyCombRepository());
```

**Singleton — RetrofitClient** (`app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java`):
- Lazy singleton, not thread-safe
- Use `RetrofitClient.createService(XxxService.class)` from all Repository constructors

**RecyclerView Adapters:**
- Extend `RecyclerView.Adapter<XxxAdapter.ViewHolder>`
- Static inner `ViewHolder` class binds view references in constructor
- `@SuppressLint("NotifyDataSetChanged")` used with full-list refresh calls
- Callback interfaces defined as inner interfaces within the Adapter class

**Hardcoded URL duplication — avoid:**
`"http://10.0.2.2:8080"` is declared as `BASE_URL` in `RetrofitClient.java` AND re-declared as
`private final String serverUrl` in `BasicEmoAdapter.java` and `TodayJellyActivity.java`.
Do not duplicate — reference `RetrofitClient.BASE_URL` or a dedicated constants class.

---

*Convention analysis: 2026-05-12*
