# Architecture Map
<!-- last_mapped: 2026-05-12 -->

## Pattern

**MVVM (Model-View-ViewModel)** with Repository pattern per domain.

```
Activity (View)
    ↓ observes LiveData
ViewModel
    ↓ delegates to
Repository
    ↓ calls via Retrofit
Service (API interface)
    ↓ HTTP
Backend REST API (http://10.0.2.2:8080/)
```

Data flows up as `LiveData<Resource<T>>` — views observe ViewModel, never call Repository directly.

## Layers

### View Layer — Activities
- `AppCompatActivity` subclasses in `*/view/` packages
- XML layouts in `res/layout/`
- Adapters (`RecyclerView.Adapter`) co-located in view packages
- Entry point: `SplashActivity` (LAUNCHER intent-filter)

### ViewModel Layer
- Extends `androidx.lifecycle.ViewModel`
- Holds `Repository` instance (constructed directly — no DI)
- Exposes `LiveData<Resource<T>>` methods mirroring Repository
- Example: `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/viewmodel/JellyViewModel.java`

### Repository Layer
- Plain Java classes in `*/retrofit/` packages (naming inconsistency — these are repositories, not Retrofit configs)
- Creates Service via `RetrofitClient.createService()`
- Wraps Retrofit callbacks in `MutableLiveData<Resource<T>>`
- Handles success/error branches inline
- Example: `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyRepository.java`

### Service Layer
- Retrofit `@interface` definitions with `@GET`, `@POST`, `@PUT`, `@DELETE` annotations
- Example: `app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyService.java`

### Common Infrastructure
- `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java` — singleton Retrofit instance, BASE_URL
- `app/src/main/java/com/mindJellyProject/mindjelly/common/Resource.java` — API response wrapper (`success`/`error` factory methods)
- `app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java` — app entry point

## Domain Boundaries

| Domain | Package | Entities |
|--------|---------|----------|
| Jelly | `jellyDomain.jelly` | Jelly, JellyResDTO, JellyDrawerResDTO, JellySaveReqDTO, JellyUpdateReqDTO, JellyUpdateResDTO |
| Jelly Combination | `jellyDomain.jellyCombination` | JellyCombination, JellyCombResDTO |
| Jelly Image | `jellyDomain.jellyImage` | JellyImage, JellyImageResDTO, JellyImageSaveReqDTO |
| Aged Emotion | `agedEmoDomain.agedEmo` | AgedEmo, AgedEmoResDTO, AgedEmoMuseumResDTO, AgedEmoSaveReqDTO, AgedEmoUpdateReqDTO |
| Aged Emotion Image | `agedEmoDomain.agedEmoImage` | AgedEmoImage, AgedEmoImageResDTO, AgedEmoImageSaveReqDTO |
| Basic Emotion | `basicEmo` | BasicEmo, BasicEmoResDTO |
| Users | `users` | Users, UserResDTO, UserSaveReqDTO, UserLoginReqDTO, UserUpdateReqDTO, UserUpdateResDTO, FindPasswordReqDTO |
| Common | `common` | RetrofitClient, Resource, SplashActivity |

## Key Abstractions

### `Resource<T>`
Generic wrapper for API call state. Two states only — no loading state tracked.
```java
Resource.success(data)   // data != null, error == null
Resource.error(message)  // data == null, error != null
```
Path: `app/src/main/java/com/mindJellyProject/mindjelly/common/Resource.java`

### `RetrofitClient`
Singleton via lazy init. Single BASE_URL for all domains — no per-domain base URL.
```java
RetrofitClient.createService(SomeService.class)
```
Path: `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java`

## Entry Points

| Entry Point | Class | Purpose |
|-------------|-------|---------|
| App Launch | `SplashActivity` | Launcher activity, splash screen |
| Home | `MainActivity` | Hub with navigation buttons |
| Auth | `LoginActivity`, `SignUpActivity` | User authentication |
| Today's Jelly | `TodayJellyActivity` | Daily emotion recording |
| Jelly Drawer | `JellyDrawerActivity` | Browse saved jellies |
| Aging Room | `AgingRoomActivity` | Aged emotion management |
| Jelly Museum | `jellyMuseumActivity` | Emotion display/museum |
| Selection Box | `JellySelectionBoxActivity` | Jelly selection UI |

## Data Flow Example (Create Jelly)

```
TodayJellyActivity
  → jellyViewModel.createJelly(reqDTO)
    → jellyRepository.createJelly(reqDTO)
      → jellyService.createJelly(reqDTO).enqueue(callback)
        → POST /api/jelly (Retrofit HTTP)
      ← onResponse: liveData.postValue(Resource.success(body))
    ← LiveData<Resource<Jelly>>
  ← observe(liveData) { resource → update UI }
```
