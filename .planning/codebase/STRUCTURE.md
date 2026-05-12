# Directory Structure
<!-- last_mapped: 2026-05-12 -->

## Root Layout

```
mindJellyAndroid/
├── app/
│   ├── src/main/
│   │   ├── java/com/mindJellyProject/mindjelly/   # All source code
│   │   ├── res/                                    # Android resources
│   │   └── AndroidManifest.xml                     # App manifest
│   └── build.gradle                                # App-level build config
├── gradle/
│   └── libs.versions.toml                          # Version catalog
├── build.gradle                                    # Project-level build config
└── .idea/                                          # Android Studio config
```

## Source Package Structure

```
com.mindJellyProject.mindjelly/
├── MainActivity.java                    # Home screen hub
├── common/
│   ├── RetrofitClient.java              # Singleton Retrofit instance
│   ├── Resource.java                    # API response wrapper
│   └── SplashActivity.java              # App launcher entry point
├── users/
│   ├── model/
│   │   ├── Users.java
│   │   ├── UserResDTO.java
│   │   ├── UserSaveReqDTO.java
│   │   ├── UserLoginReqDTO.java
│   │   ├── UserUpdateReqDTO.java
│   │   ├── UserUpdateResDTO.java
│   │   └── FindPasswordReqDTO.java
│   ├── retrofit/
│   │   ├── UserService.java             # Retrofit API interface
│   │   └── UserRepository.java          # Data access layer
│   ├── view/
│   │   ├── LoginActivity.java
│   │   ├── SignUpActivity.java
│   │   ├── ProfileActivity.java
│   │   ├── SettingActivity.java
│   │   ├── FindEmailActivity.java
│   │   └── FindPasswordActivity.java
│   └── viewmodel/
│       └── UserViewModel.java
├── basicEmo/
│   ├── model/
│   │   ├── BasicEmo.java
│   │   └── BasicEmoResDTO.java
│   ├── retrofit/
│   │   ├── BasicEmoService.java
│   │   └── BasicEmoRepository.java
│   ├── view/
│   │   ├── TodayJellyActivity.java
│   │   └── BasicEmoAdapter.java
│   └── viewmodel/
│       └── BasicEmoViewModel.java
├── jellyDomain/
│   ├── jelly/
│   │   ├── model/
│   │   │   ├── Jelly.java
│   │   │   ├── JellyResDTO.java
│   │   │   ├── JellyDrawerResDTO.java
│   │   │   ├── JellySaveReqDTO.java
│   │   │   ├── JellyUpdateReqDTO.java
│   │   │   └── JellyUpdateResDTO.java
│   │   ├── retrofit/
│   │   │   ├── JellyService.java
│   │   │   └── JellyRepository.java
│   │   ├── view/
│   │   │   ├── JellyDrawerActivity.java
│   │   │   ├── JellyDrawerAdapter.java
│   │   │   └── JellySelectionBoxActivity.java
│   │   └── viewmodel/
│   │       └── JellyViewModel.java
│   ├── jellyCombination/
│   │   ├── model/
│   │   │   ├── JellyCombination.java
│   │   │   └── JellyCombResDTO.java
│   │   ├── retrofit/
│   │   │   ├── JellyCombService.java
│   │   │   └── JellyCombRepository.java
│   │   └── viewmodel/
│   │       └── JellyCombViewModel.java
│   └── jellyImage/
│       ├── model/
│       │   ├── JellyImage.java
│       │   ├── JellyImageResDTO.java
│       │   └── JellyImageSaveReqDTO.java
│       ├── retrofit/
│       │   ├── JellyImageService.java
│       │   └── JellyImageRepository.java
│       └── viewmodel/
│           └── JellyImageViewModel.java
└── agedEmoDomain/
    ├── agedEmo/
    │   ├── model/
    │   │   ├── AgedEmo.java
    │   │   ├── AgedEmoResDTO.java
    │   │   ├── AgedEmoMuseumResDTO.java
    │   │   ├── AgedEmoSaveReqDTO.java
    │   │   ├── AgedEmoUpdateReqDTO.java
    │   │   └── AgedEmoUpdateResDTO.java
    │   ├── retrofit/
    │   │   ├── AgedEmoService.java
    │   │   └── AgedEmoRepository.java
    │   ├── view/
    │   │   ├── AgingRoomActivity.java
    │   │   └── jellyMuseumActivity.java
    │   └── viewmodel/
    │       └── AgedEmoViewModel.java
    └── agedEmoImage/
        ├── model/
        │   ├── AgedEmoImage.java
        │   ├── AgedEmoImageResDTO.java
        │   └── AgedEmoImageSaveReqDTO.java
        ├── retrofit/
        │   ├── AgedEmoImageService.java
        │   └── AgedEmoImageRepository.java
        └── viewmodel/
            └── AgedEmoImageViewModel.java
```

## Resource Layout

```
res/
├── drawable/
│   └── basic_background.png
├── layout/
│   ├── activity_main.xml
│   ├── activity_today_jelly.xml
│   ├── activity_jelly_drawer.xml
│   ├── item_basic_emo.xml
│   └── item_jelly_drawer.xml
├── mipmap-*/                    # App icons (multiple densities)
├── values/
│   ├── strings.xml
│   ├── colors.xml
│   └── themes/
└── xml/
    ├── backup_rules.xml
    └── data_extraction_rules.xml
```

## Naming Conventions

| Layer | Convention | Example |
|-------|-----------|---------|
| Activity | `*Activity` (PascalCase) | `TodayJellyActivity`, `jellyMuseumActivity` (inconsistent) |
| ViewModel | `*ViewModel` | `JellyViewModel` |
| Repository | `*Repository` | `JellyRepository` |
| Service (Retrofit) | `*Service` | `JellyService` |
| Request DTO | `*ReqDTO` | `JellySaveReqDTO`, `JellyUpdateReqDTO` |
| Response DTO | `*ResDTO` | `JellyResDTO`, `JellyDrawerResDTO` |
| Domain model | No suffix | `Jelly`, `Users`, `BasicEmo` |
| Adapter | `*Adapter` | `JellyDrawerAdapter`, `BasicEmoAdapter` |
| Layout files | `activity_*`, `item_*` | `activity_jelly_drawer.xml`, `item_jelly_drawer.xml` |

## Key File Locations

| Purpose | Path |
|---------|------|
| App entry point | `app/src/main/java/com/mindJellyProject/mindjelly/common/SplashActivity.java` |
| Home screen | `app/src/main/java/com/mindJellyProject/mindjelly/MainActivity.java` |
| Network client | `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java` |
| Response wrapper | `app/src/main/java/com/mindJellyProject/mindjelly/common/Resource.java` |
| App manifest | `app/src/main/AndroidManifest.xml` |
| Build config | `app/build.gradle` |
| Version catalog | `gradle/libs.versions.toml` |
