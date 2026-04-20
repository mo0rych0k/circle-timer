---
name: server-ktor-orchestrator
description: Orchestrate server-side work in this repository's Ktor module. Use when editing routes, Application.module wiring, Ktor plugins, server tests, or server-side dependencies under server/.
---

# Server Ktor Orchestrator

## Goal

Use this skill as the explicit entry point for work under `server/**`.

It should help the agent:

- keep Ktor bootstrap and route wiring focused,
- use the repository's existing `testApplication` testing pattern,
- avoid leaking server-only concerns into shared KMP modules,
- finish with repo-specific verification.

## Workflow

### 1) Classify the server change

Determine whether the task affects:

- bootstrap or `main()`
- `Application.module()` wiring
- routing
- Ktor plugins
- server dependencies
- server tests

### 2) Check module placement

Before editing, confirm whether the code belongs in `server/` or should live in `common/**`.

Move code to `common/**` only when it is genuinely reusable outside the server module. If you do
that, invoke `.cursor/skills/common-module-orchestrator/SKILL.md`.

### 3) Keep server wiring readable

- Prefer small focused server entry points.
- If `Application.module()` grows across multiple concerns, extract route or plugin setup helpers.
- Keep HTTP behavior explicit in routing code.

### 4) Use the repo's server testing style

- Prefer `testApplication { ... }`.
- Assert status codes and response bodies directly.
- Keep tests in `server/src/test/kotlin`.

### 5) Finish with verification

Before declaring the server change complete:

- invoke `.cursor/skills/repo-verification-orchestrator/SKILL.md`
- run targeted Gradle verification for `:server`
- mention whether server-only code stayed isolated or whether shared-module consumers were also
  revalidated
