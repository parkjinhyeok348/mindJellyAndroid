---
name: phase-planner
description: Use when a request needs multiple steps, touches 2 or more files/modules, or needs durable planning. Create or update docs/ai-work/02-roadmap.md and docs/ai-work/phases/phase-NNN-*.md using a small phase-based plan.
---

# Phase Planner

## Purpose

Split non-trivial work into small, verifiable phases using the project-local `docs/ai-work` files.

## Rules

- Keep each phase small enough to implement and verify independently.
- Reuse existing roadmap and phase files when they already exist.
- Do not import a large command system or external workflow vocabulary.
- Record decisions only when they affect later implementation.
- Hand off the next actionable phase to `phase-executor`.

## Required Files

- `docs/ai-work/02-roadmap.md`
- `docs/ai-work/phases/phase-NNN-*.md`

## Phase File Shape

Use this minimal structure:

```markdown
# Phase NNN: Title

## Goal

## Scope

## Tasks

## Verification

## Completion Criteria
```
