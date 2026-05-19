# Phase 001: Confirm Current Data Flow

## Goal

Confirm what already exists for showing the logged-in user's jelly list on JellyDrawer, and identify the smallest implementation gap.

## Scope

Included:
- `SessionManager` user ID retrieval.
- `JellyDrawerActivity` list loading and empty/error states.
- `JellyViewModel`, `JellyRepository`, and `JellyService` list flow.
- `JellyDrawerResDTO`, `JellyDrawerAdapter`, and JellyDrawer item layout fields.

Excluded:
- Backend implementation.
- Large UI redesign.
- New navigation flows outside JellyDrawer.

## Tasks

- [x] Verify JellyDrawer reads the current session user ID and handles missing login.
- [x] Verify the existing jelly list API path and response DTO fields.
- [x] Verify the adapter displays enough information for a user to recognize their added jellies.
- [x] Decide whether Phase 002 needs code changes or only behavior verification.

## Findings

- `JellyDrawerActivity` reads `SessionManager.getUserId()`, redirects to login when it is `-1L`, and calls `jellyViewModel.getJellyList(userId)` for valid sessions.
- `JellyViewModel`, `JellyRepository`, and `JellyService` already expose a list flow through `GET jelly/user/{userId}` returning `List<JellyDrawerResDTO>`.
- `JellyDrawerResDTO` includes `jellyId`, `jellyCombId`, `isAging`, `createDate`, `emo1Name`, `emo1Icon`, `emo2Name`, `emo2Icon`, and `status`.
- `JellyDrawerAdapter` currently displays create date, two emotion icons, status, and start-aging action. It does not display `emo1Name` or `emo2Name`.
- The current DTO does not contain a `jellyName` or content field, so showing a user-defined jelly name is not supported by the observed Android contract.

## Phase 002 Target

Phase 002 should make a small JellyDrawer presentation update using fields already present in `JellyDrawerResDTO`, especially emotion names if the item needs to be more recognizable. Adding user-defined jelly names should remain out of scope unless the backend/API contract is confirmed to provide that field.

## Verification

- [x] Read the relevant files and capture exact implementation gaps.
- [x] Check existing unit tests related to JellyDrawer DTO, adapter diffing, and Retrofit endpoints.

## Completion Criteria

- The current flow is documented.
- Phase 002 has a clear, bounded implementation target.
