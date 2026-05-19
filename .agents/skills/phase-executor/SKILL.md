---
name: phase-executor
description: Use when executing a planned phase from docs/ai-work/phases. Implement only the current phase scope, verify the result, and record a concise session history entry under docs/ai-work/history without modifying unrelated project code.
---

# Phase Executor

## Purpose

Execute one planned phase at a time and keep the project record current.

## Rules

- Read the relevant `docs/ai-work/phases/phase-NNN-*.md` before editing.
- Stay inside the current phase scope unless the user expands it.
- Do not modify unrelated project code.
- Verify with the smallest meaningful check for the change.
- Update the phase file status or checklist when useful.
- Add a concise history entry after the phase with changes, decisions, and verification.

## Required History Entry

Write or append to `docs/ai-work/history/YYYY-MM-DD-session.md`:

```markdown
## HH:mm - Phase NNN

- Changes:
- Decisions:
- Verification:
- Remaining:
```
