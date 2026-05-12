# Testing Patterns

**Analysis Date:** 2026-05-12

## Test Framework

**Runner:**
- JUnit 4 (`junit:junit:4.13.2`) for local unit tests
- AndroidJUnit4 (`androidx.test.ext:junit:1.1.5`) for instrumented tests
- Config: `app/build.gradle` — `testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"`

**Assertion Library:**
- `org.junit.Assert.*` (JUnit 4 static assertions)

**UI Testing:**
- Espresso (`androidx.test.espresso:espresso-core:3.5.1`) declared as dependency but not yet used

**Run Commands:**
```bash
./gradlew test                   # Run local unit tests
./gradlew connectedAndroidTest   # Run instrumented tests on device/emulator
./gradlew testDebugUnitTest      # Run debug variant unit tests
```

## Test File Organization

**Location:**
- Local unit tests: `app/src/test/java/com/mindJellyProject/mindjelly/`
- Instrumented tests: `app/src/androidTest/java/com/mindJellyProject/mindjelly/`
- Tests are NOT co-located with source — they live in separate source sets

**Naming:**
- Test classes: `[Subject]Test.java` or `Example[Type]Test.java`
- Current files:
  - `app/src/test/java/com/mindJellyProject/mindjelly/ExampleUnitTest.java`
  - `app/src/androidTest/java/com/mindJellyProject/mindjelly/ExampleInstrumentedTest.java`

**Structure:**
```
app/src/
├── main/java/com/mindJellyProject/mindjelly/   # Production code
├── test/java/com/mindJellyProject/mindjelly/   # Local unit tests
└── androidTest/java/com/mindJellyProject/mindjelly/  # Instrumented tests
```

## Test Structure

**Current state:** Only the auto-generated scaffold tests exist. No domain-specific tests have been written.

**Local unit test pattern (from ExampleUnitTest.java):**
```java
package com.mindJellyProject.mindjelly;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}
```

**Instrumented test pattern (from ExampleInstrumentedTest.java):**
```java
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.mindJellyProject.mindjelly", appContext.getPackageName());
    }
}
```

**Patterns:**
- `@Test` annotation on each test method
- `@RunWith(AndroidJUnit4.class)` required for instrumented tests
- No `@Before` / `@After` setup/teardown observed (scaffold only)
- No `@Rule` usage observed

## Mocking

**Framework:** None configured. No Mockito, MockK, or Robolectric dependency in `gradle/libs.versions.toml` or `app/build.gradle`.

**What to mock when adding tests:**
- `RetrofitClient` / `XxxService` — to avoid real network calls in unit tests
- `LiveData` observers — use `InstantTaskExecutorRule` for synchronous LiveData testing
- Android `Context` — use `InstrumentationRegistry` in instrumented tests or Robolectric for unit tests

**Recommended additions to `gradle/libs.versions.toml`:**
```toml
mockito = "5.x.x"
robolectric = "4.x.x"
arch-core-testing = "2.x.x"   # for InstantTaskExecutorRule
```

## Fixtures and Factories

**Test data:** None defined. No fixture files or factory classes exist.

**Recommended pattern for future tests:**
```java
// Create test DTOs inline in test methods
AgedEmoSaveReqDTO testDto = new AgedEmoSaveReqDTO(...);
AgedEmo testAgedEmo = new AgedEmo(1L, 1L, 1L, "testName", "content", "2025-01-01", new ArrayList<>());
```

**Location for shared fixtures:** Create under `app/src/test/java/com/mindJellyProject/mindjelly/fixtures/` when needed.

## Coverage

**Requirements:** None enforced. No JaCoCo or coverage threshold configuration detected.

**View coverage report:**
```bash
./gradlew testDebugUnitTest jacocoTestReport   # requires JaCoCo setup first
```

## Test Types

**Unit Tests (`app/src/test/`):**
- Scope: JVM-only, no Android framework
- Appropriate for: `Resource<T>` logic, Repository callback wiring, ViewModel delegation, DTO serialization
- Currently: only scaffold `ExampleUnitTest.java` exists

**Instrumented Tests (`app/src/androidTest/`):**
- Scope: runs on Android device or emulator
- Appropriate for: Activity UI flows, RecyclerView adapter rendering, ViewBinding correctness
- Currently: only scaffold `ExampleInstrumentedTest.java` exists

**E2E Tests:** Not configured.

## Common Patterns (Recommended for this codebase)

**Testing Resource wrapper logic (unit test):**
```java
@Test
public void resource_success_isSuccess() {
    Resource<String> resource = Resource.success("data");
    assertTrue(resource.isSuccess());
    assertFalse(resource.isError());
    assertEquals("data", resource.getData());
}

@Test
public void resource_error_isError() {
    Resource<String> resource = Resource.error("something went wrong");
    assertTrue(resource.isError());
    assertFalse(resource.isSuccess());
    assertEquals("something went wrong", resource.getError());
}
```

**Testing LiveData from Repository (requires InstantTaskExecutorRule):**
```java
@Rule
public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

@Test
public void createAgedEmo_onSuccess_postsSuccess() {
    // Arrange: mock AgedEmoService to return a successful response
    // Act: call repository.createAgedEmo(dto)
    // Assert: observe LiveData, verify Resource.isSuccess()
}
```

**Async testing note:** Retrofit callbacks run on background threads. Use
`InstantTaskExecutorRule` (from `androidx.arch.core:core-testing`) to make LiveData
post values synchronously in tests.

## What Has No Test Coverage (as of 2026-05-12)

- All Repository classes (`AgedEmoRepository`, `JellyRepository`, `UserRepository`, etc.)
- All ViewModel classes (`AgedEmoViewModel`, `BasicEmoViewModel`, `UserViewModel`, etc.)
- `Resource<T>` utility class (`common/Resource.java`)
- `RetrofitClient` singleton (`common/RetrofitClient.java`)
- All Activity UI flows (`LoginActivity`, `TodayJellyActivity`, etc.)
- All RecyclerView Adapters (`BasicEmoAdapter`, `JellyDrawerAdapter`)

---

*Testing analysis: 2026-05-12*
