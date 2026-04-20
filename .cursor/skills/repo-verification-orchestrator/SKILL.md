---
name: repo-verification-orchestrator
description: Run repository-specific verification for this Kotlin Multiplatform project. Use before claiming feature or shared-module work is complete, especially after Gradle module changes, app wiring changes, Koin integration changes, or multi-module edits.
---

# Repo Verification Orchestrator

## Goal

Use this skill instead of generic verification flows.

This repository is Gradle- and Kotlin-first, so verification should focus on:

- targeted Gradle tasks,
- KMP module correctness,
- app root wiring,
- shared-module consumers,
- structure validation for newly added modules.

## Inputs

Collect:

- touched paths
- whether `settings.gradle.kts` changed
- whether `composeApp/build.gradle.kts` changed
- whether app DI or app navigation wiring changed
- whether new modules were added

## Workflow

### 1) Build the minimum useful verification set

Choose the narrowest sensible verification:

- single feature UI/domain/data/data-network change:
    - run Gradle tasks for those modules only
- shared module change:
    - run the shared module plus at least one representative consumer
- root app wiring change:
    - include `composeApp`
- broad cross-module refactor:
    - run a wider repo check if targeted checks are insufficient

### 2) Run structure validation when relevant

If the change adds or wires modules, execute:

```bash
python ".cursor/skills/repo-verification-orchestrator/scripts/check_module_structure.py"
```

Use `--feature <name>` when validating a specific feature addition.

Also validate orchestration coverage when rules or project skills changed:

```bash
python ".cursor/skills/repo-verification-orchestrator/scripts/check_rule_orchestration.py"
```

### 3) Prefer targeted Gradle verification

Good examples:

- `./gradlew :features:weather:weather-ui:check`
- `./gradlew :features:weather:weather-data:check`
- `./gradlew :common:core-navigation:check :composeApp:check`

If `check` is unavailable or too broad for the module, choose the closest available verification
task and explain why.

### 4) Review app/root integration explicitly

When module structure or navigation changed, explicitly inspect:

- `settings.gradle.kts`
- `composeApp/build.gradle.kts`
- `composeApp/src/commonMain/kotlin/io/pylyp/sample/composeapp/di/AppModules.kt`
- `common/core-navigation`
- `composeApp/.../roating`

### 5) Summarize evidence, not guesses

Report:

- which commands were run
- which modules were verified
- whether the structure script passed
- whether the rule orchestration script passed
- any remaining gaps that were not verified

Do not claim success without command evidence.
