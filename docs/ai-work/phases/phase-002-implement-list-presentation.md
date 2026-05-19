# Phase 002: Implement List Presentation

## Goal

Implement the smallest missing JellyDrawer changes needed for the logged-in user to clearly view the jellies they added.

## Scope

Included:
- JellyDrawer list rendering improvements.
- DTO/adapter/layout changes if the current item view does not show enough identifying jelly information.
- Error, loading, and empty-state behavior only where directly affected.

Excluded:
- Backend API redesign.
- Unrelated jelly creation/editing flows.
- Broad visual redesign.

## Tasks

- [x] Update JellyDrawer item UI only if Phase 001 finds missing display fields.
- [x] Update `JellyDrawerResDTO` only if the API already returns fields the app is not mapping.
- [x] Update `JellyDrawerAdapter` binding and diff behavior for any newly displayed fields.
- [x] Keep existing start-aging behavior intact.

## Result

- Added `tvEmotionNames` to the JellyDrawer item layout so each row can show the emotion-name pair already available on `JellyDrawerResDTO`.
- Bound `emo1Name` and `emo2Name` in `JellyDrawerAdapter`, trimming blank values and showing only available names.
- Updated adapter diff content checks to include displayed emotion names and icon paths.
- Left `JellyDrawerResDTO`, API routes, loading, empty-state, error-state, and start-aging behavior unchanged.

## Verification

- [x] Run focused unit tests for DTO/adapter behavior if touched.
- [x] Run a targeted Android test/build command if available and reasonably scoped.

Command:

```powershell
.\gradlew.bat testDebugUnitTest --tests com.mindJellyProject.mindjelly.jellyDomain.jelly.view.JellyDrawerAdapterDiffTest
```

Result: passed.

## Completion Criteria

- Logged-in user jelly list renders with recognizable item information.
- Empty, loading, and error states still behave as expected.
- No unrelated project code is changed.
