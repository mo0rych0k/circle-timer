---
name: module-scaffolding-orchestrator
description: Orchestrate creation of plain repository modules that are not full feature scaffolds. Use when adding a new common module, utility module, persistence support module, or other Gradle module that must be placed correctly, wired into settings.gradle.kts, and verified with repo-specific checks.
---

# Module Scaffolding Orchestrator

## Goal

Use this skill when the task is to create a module, but not a full `features/<featureName>`
scaffold.

Examples:

- a new `common/core-*` module
- a new `common/utils/*` module
- a new support module under `common/persistence`
- another shared Gradle module that should not be treated as a full feature

If the request is for a feature with `*-ui`, `*-domain`, `*-data`, and `*-data-network`, use
`.cursor/skills/feature-scaffolding-orchestrator/SKILL.md` instead.

## Workflow

### 1) Classify the module type

Determine which family the new module belongs to:

- `common/core-*`
- `common/uikit`
- `common/utils/*`
- `common/persistence/*`
- app support module
- other repository shared module

Do not create a new module until you confirm the code does not fit cleanly into an existing one.

### 2) Define placement and boundaries

Before scaffolding, answer:

1. Why is a new module needed instead of extending an existing one?
2. Which modules may depend on it?
3. Which modules must it never depend on?
4. Does it need platform-specific `expect/actual` support?

For `common/**` modules, follow `.cursor/skills/common-module-orchestrator/SKILL.md` as the boundary
authority.

### 3) Scaffold the module

Create the minimal required structure:

- module directory
- `build.gradle.kts`
- `src/commonMain` and test source sets as needed
- package and namespace aligned with the module purpose

Prefer the repository's existing Gradle and package conventions:

- `alias(libs.plugins.app.kotlinMultiplatform)` for shared KMP modules
- `androidLibrary { namespace = ... }`
- explicit API compatibility with the repo convention plugin

### 4) Wire the module into the repo

Update the required root files:

1. `settings.gradle.kts`
2. consumer module `build.gradle.kts` files
3. app or shared DI modules if the new module contributes DI bindings

Only add app-level wiring when the module is truly consumed there.

### 5) Validate with the right orchestrators

- If the new module is under `common/**`, invoke
  `.cursor/skills/common-module-orchestrator/SKILL.md`.
- If the new module affects an existing feature, invoke
  `.cursor/skills/feature-edit-review-orchestrator/SKILL.md` for the impacted categories.
- Always finish with `.cursor/skills/repo-verification-orchestrator/SKILL.md`.

## Output

Summarize:

- why a new module was justified
- which root files were updated
- which consumers were wired to the module
- which verification steps were run
