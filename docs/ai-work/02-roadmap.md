# JellyDrawer User Jelly List Roadmap

## Goal

Add and verify the JellyDrawer flow that lets the currently logged-in user view the jellies they created.

## Phases

1. Phase 001: Confirm current data flow
   - Trace session user ID, JellyDrawer loading, API endpoint, repository, ViewModel, adapter, and layout.
   - Decide whether the existing list fields are sufficient for the requested "view my jellies" experience.

2. Phase 002: Implement missing list presentation
   - Fill any UI/data gaps found in Phase 001.
   - Keep the feature scoped to JellyDrawer and the existing jelly list API unless evidence shows a backend contract mismatch.

3. Phase 003: Verify and record outcome
   - Add or update focused tests for the touched behavior.
   - Run the smallest meaningful verification command available in the Android project.
   - Record changes, decisions, verification, and remaining risk in `docs/ai-work/history/`.

## Current Next Phase

Start with `docs/ai-work/phases/phase-001-initial.md`.
