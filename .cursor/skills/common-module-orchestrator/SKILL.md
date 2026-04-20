---
name: common-module-orchestrator
description: Orchestrate edits in shared common modules for this KMP repository. Use when changing code under common/, deciding where shared logic belongs, updating shared DI or navigation primitives, or validating common-module boundary decisions before implementation.
---

# Common Module Orchestrator

## Goal

Use this skill as the explicit entry point for work under `common/**`.

It should help the agent:

- place new shared code in the correct common module,
- keep shared modules feature-agnostic,
- identify which downstream app or feature modules must be revalidated,
- finish with repo-specific verification.

## Workflow

### 1) Classify the change

First determine which shared area the change belongs to:

- `core-di`
- `core-navigation`
- `core-network`
- `core-threading`
- `core-domain`
- `core-ui`
- `uikit`
- `resources`
- `app-info`
- `persistence-database`
- `utils/*`

If the requested code is feature-specific, stop and move the change back into the relevant feature
module instead of `common/**`.

### 2) Check boundary fit

Before editing, answer these questions:

1. Is this code reused by multiple features or app entry points?
2. Can it stay feature-agnostic?
3. Does it belong in a narrower existing module instead of creating a broader utility?
4. Does it require `expect/actual` instead of direct platform coupling?

Only proceed when the placement is justified.

### 3) Choose the minimum required validations

Use the smallest relevant validation pass:

- DI or factory changes:
    - review `composeApp/.../di`
    - review feature DI modules that consume the abstraction
- navigation changes:
    - review `common/core-navigation`
    - review `composeApp/.../roating`
    - review affected feature roots
- threading changes:
    - review `common/core-threading`
    - verify at least one `*-data-network` and one test consumer
- persistence changes:
    - review `common/persistence/persistence-database`
    - verify affected `*-data` modules
- UI foundation changes:
    - review `common/core-ui` or `common/uikit`
    - verify representative feature UI consumers

### 4) Escalate to feature reviewers when needed

If a shared-module change impacts feature behavior, explicitly invoke the matching repository
reviewer skill or subagent path:

- feature UI impact:
    - `.cursor/skills/feature-edit-review-orchestrator/SKILL.md`
    - categories: `ui-navigation-di` and/or `ui-mvi-state-wiring`
- domain contract impact:
    - `domain-layer-usecases-resource-reviewer`
- network or repository impact:
    - `data-network-layer-ktor-dispatchers-reviewer`
    - `data-layer-repository-mappers-reviewer`
- persistence impact:
    - `data-storage-layer-room-reviewer`

Do not invoke unrelated reviewers.

## Completion

Before declaring the shared-module work complete:

- invoke `.cursor/skills/repo-verification-orchestrator/SKILL.md`
- run targeted Gradle verification for the changed shared modules and at least one representative
  consumer
- mention the shared module chosen, why it was the correct placement, and which consumers were
  revalidated
