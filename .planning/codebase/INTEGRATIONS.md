# External Integrations

**Analysis Date:** 2026-05-12

## APIs & External Services

**Backend REST API (Custom):**
- MindJelly backend server - all application data (users, jellies, emotions, images)
  - SDK/Client: Retrofit 2.9.0 via `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java`
  - Base URL: `http://10.0.2.2:8080/` (hardcoded; emulator loopback to localhost)
  - Auth: No token/header auth detected in current code; login returns a String response from `POST /users/login`
  - Transport: HTTP (cleartext); TLS not configured

**API Endpoints by Domain:**

Users (`app/src/main/java/com/mindJellyProject/mindjelly/users/retrofit/UserService.java`):
- `POST /users` - create user
- `PUT /users/{userId}` - update user
- `GET /users/{userId}/profile` - get user profile
- `GET /users/check-email` - duplicate email check
- `GET /users/check-phone` - duplicate phone check
- `GET /users/check-nickname` - duplicate nickname check
- `POST /users/find-email` - find email by name + phone
- `POST /users/find-password` - find password
- `POST /users/login` - login (returns String token/response)
- `DELETE /users/{userId}` - delete user

Jelly (`app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jelly/retrofit/JellyService.java`):
- `POST /jelly` - create jelly
- `GET /jelly/{jellyId}/info` - get jelly update info
- `PUT /jelly/{jellyId}` - update jelly
- `GET /jelly/{jellyId}` - get jelly by ID
- `GET /jelly/user/{userId}` - get jelly list for user

JellyImage (`app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyImage/retrofit/JellyImageService.java`):
- `POST /jellyImage` - create jelly image
- `GET /jellyImage/{jellyId}` - get jelly image list

JellyCombination (`app/src/main/java/com/mindJellyProject/mindjelly/jellyDomain/jellyCombination/retrofit/JellyCombService.java`):
- `GET /jellyComb/{jellyCombId}` - get combination by ID
- `GET /jellyComb/jelly-icon/{firstEmo}/{secondEmo}/{isAwaken}` - get jelly icon path

AgedEmo (`app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmo/retrofit/AgedEmoService.java`):
- `POST /agedEmo` - create aged emotion
- `GET /agedEmo/{agedEmoId}/update-info` - get aged emotion update info
- `PUT /agedEmo/{agedEmoId}` - update aged emotion
- `GET /agedEmo/{agedEmoId}` - get aged emotion by ID
- `GET /agedEmo/user/{userId}` - get aged emotion list for user

AgedEmoImage (`app/src/main/java/com/mindJellyProject/mindjelly/agedEmoDomain/agedEmoImage/retrofit/AgedEmoImageService.java`):
- `POST /agedEmoImage` - create aged emotion image
- `GET /agedEmoImage/{agedEmoId}` - get aged emotion image list

BasicEmo (`app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/retrofit/BasicEmoService.java`):
- `GET /basicEmo/{emoId}` - get basic emotion by ID
- `GET /basicEmo` - get all basic emotions

## Data Storage

**Databases:**
- Remote only - all data stored on the backend server via REST API
- No local SQLite or Room database detected

**File Storage:**
- Remote image URLs served from the backend API server
- Images loaded at runtime via Glide from URL paths returned by the API
  - Pattern: base URL + icon path string (used in `app/src/main/java/com/mindJellyProject/mindjelly/basicEmo/view/TodayJellyActivity.java`)
- AWS S3 SDK (`com.amazonaws:aws-java-sdk-s3:1.12.767`) is declared in `gradle/libs.versions.toml` but NOT added to `app/build.gradle` - not active in the app

**Caching:**
- None detected; no SharedPreferences, Room, or cache configuration found
- Glide provides in-memory and disk cache for images by default

## Authentication & Identity

**Auth Provider:**
- Custom backend authentication
  - Login endpoint: `POST /users/login` in `app/src/main/java/com/mindJellyProject/mindjelly/users/retrofit/UserService.java`
  - Login returns a `String` (likely a token or session identifier)
  - No token storage mechanism detected in current source (no SharedPreferences, no header injection in RetrofitClient)
  - No OAuth, Firebase Auth, or third-party auth SDK present

## Monitoring & Observability

**Error Tracking:**
- None detected; no Crashlytics, Sentry, or similar SDK present

**Logs:**
- Standard Android Logcat (no structured logging library detected)
- Errors surfaced via `Resource.error(String)` wrapper class at `app/src/main/java/com/mindJellyProject/mindjelly/common/Resource.java`

## CI/CD & Deployment

**Hosting:**
- Android app distributed via APK/Play Store (standard Android)
- Backend hosted locally during development (`10.0.2.2:8080`)

**CI Pipeline:**
- None detected; no `.github/workflows/`, `Jenkinsfile`, or CI config files found

## Environment Configuration

**Required configuration:**
- Backend server must be running and reachable at `http://10.0.2.2:8080/` (emulator) or a configured host
- No required environment variables; base URL is hardcoded in `app/src/main/java/com/mindJellyProject/mindjelly/common/RetrofitClient.java`

**Secrets location:**
- No secrets management detected; no `.env`, keystore references, or secrets files found in source

## Webhooks & Callbacks

**Incoming:**
- None detected

**Outgoing:**
- None detected

---

*Integration audit: 2026-05-12*
