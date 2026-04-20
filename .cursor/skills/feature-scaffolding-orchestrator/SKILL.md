---
name: feature-scaffolding-orchestrator
description: Orchestrate new feature creation for this KMP + Decompose + MVI repository. Use when creating a new feature, scaffolding feature modules, or adding a feature that must be wired into app navigation, app DI, Gradle module registration, and repo-specific verification.
origin: project-template
---

# Feature Scaffolding Orchestrator

## Trigger

Use this skill when the user asks to:

- “create a new feature”
- “scaffold a feature module”
- “add a feature screen / layer” and the work should follow the repository’s KMP + Decompose + MVI +
  layer separation rules.

## Goal

Coordinate multiple specialized subagents so the resulting feature is:

- scaffolded according to `.cursor/rules/feature-module-structure.mdc`
- integrated into global app navigation via `AppFeature` and `AppRootConfig`
- integrated into repo module wiring via `settings.gradle.kts`, `composeApp/build.gradle.kts`, and
  `composeApp/.../di/AppModules.kt`
- validated per layer (UI navigation+MVI DI, domain use-case/resource flow, data-network dispatcher
  usage, repository mapping boundaries)
- verified with the repo verification skill before completion

## Workflow

### 1) Intake (ask only what’s missing)

Ask the user for:

- `featureName`
- whether to add an optional `*-ui-components` module
- what the feature needs to show (e.g., screens, network endpoints, persistence needs)

Derive:

- module paths: `features/<featureName>/<featureName>-{ui,domain,data,data-network}`
- package namespaces: `io.pylyp.<featureName>.<layer>`
- whether the feature is intentionally UI-only or must be full-stack

### 2) Scaffold the feature modules

Launch subagent:

- `subagent_type="feature-module-generator"`

Rules for this step:

- Ensure `*-domain`, `*-data`, and `*-data-network` are scaffolded (create or reuse existing modules
  as required by the template rules).
- Do NOT leave a feature only with `*-ui` unless the other modules already exist.

### 2.5) Wire the feature into the repository root

Update all required integration points:

1. `settings.gradle.kts`
    - include the new feature modules
2. `composeApp/build.gradle.kts`
    - add feature module dependencies used by the app
3. `composeApp/src/commonMain/kotlin/io/pylyp/sample/composeapp/di/AppModules.kt`
    - add feature Koin modules
4. `common/core-navigation`
    - add `AppFeature` entry when the feature is reachable through global navigation
5. `composeApp/.../roating/AppRootComponent.kt`
    - add `Child`, `AppRootConfig`, and `child(...)` wiring when needed
6. `composeApp/.../roating/mapper/AppRootMapper.kt`
    - map `AppFeature` to the app root config

### 3) UI + Navigation + DI validation gate

After scaffolding and/or UI implementation:
Launch reviewer subagents (in order):

1. `subagent_type="ui-layer-decompose-mvi-navigation-di-reviewer"` (Validate Decompose root,
   `AppFeature` navigation contract, isolated DI)
2. `subagent_type="ui-domain-mvi-state-methods-reviewer"` (Validate State/Intent/Label and store->
   component->screen wiring)

If issues are found:

- iterate by applying the minimal fixes and re-running the same reviewers

### 4) Domain layer validation gate

Launch:

- `subagent_type="domain-layer-usecases-resource-reviewer"`

Focus:

- resource/loading/error mapping consistency
- domain doesn’t depend on UI/Data/Network

### 5) Data + Data-network validation gate

Launch:

1. `subagent_type="data-network-layer-ktor-dispatchers-reviewer"` (Check dispatcher usage in Ktor
   remote sources)
2. `subagent_type="data-layer-repository-mappers-reviewer"` (Check mapping boundaries:
   network/DTO -> domain entities)

### 6) Optional end-to-end MVI quality loop

If the feature includes non-trivial MVI UI wiring:
Launch:

- `subagent_type="mvi-edit-review-loop"`

Use it to:

- ensure Compose/Decompose UI is generated and wired in a single consistent loop

## 7) Repo verification gate

Before declaring the scaffolding complete:

- invoke `.cursor/skills/repo-verification-orchestrator/SKILL.md`
- run targeted Gradle checks for the touched modules
- run the structure check script if modules, packages, or root integration changed

## Output requirements

When finished, summarize:

- feature modules created/reused
- where global navigation integration was updated (`AppFeature`, `AppRootComponent`,
  `AppRootMapper`, `app di modules`)
- where root Gradle/app wiring was updated (`settings.gradle.kts`, `composeApp/build.gradle.kts`)
- which reviewers/enforcers were run and whether they passed (or what was fixed)

