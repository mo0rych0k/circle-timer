---
name: feature-edit-review-orchestrator
description: Choose and run the minimal set of reviewer or enforcer subagents for an existing feature in this KMP + Decompose + MVI repository. Use when editing, validating, or reviewing feature changes across UI navigation, MVI wiring, domain Resource/Error policies, data mappers, data-network dispatchers, persistence, and final repo verification.
origin: project-template
---

# Feature Edit Review Orchestrator

## Trigger

Use this skill when the user:

- asks to “review and fix” a feature
- asks to “edit a feature” and wants only the relevant checks
- reports errors and wants targeted validation (instead of running everything)

## Goal

Run the smallest set of the repository’s specialized reviewer/enforcer subagents required to verify
the feature changes:

- UI layer correctness (Decompose + MVI + navigation/DI contract)
- UI MVI state/intent/label wiring correctness
- Domain use-case Resource/Error policy correctness
- Data-network dispatcher usage correctness
- Data repository mapping boundaries correctness
- Optional persistence/Room correctness (if the feature touches DB)

## Workflow

### 1) Intake: classify the change

Ask the user to choose one or more “change categories”:

- `ui-navigation-di` (navigation, Decompose root, `AppFeature` mapping, DI boundaries in UI)
- `ui-mvi-state-wiring` (State/Intent/Label modeling, Store -> Component -> Screen wiring)
- `domain-resource-errors` (Resource.Loading/Success/Error mapping rules in use-cases)
- `data-network-dispatchers` (network calls and dispatcher usage in data-network)
- `data-repository-mappers` (DTO/entity mapping boundaries in data)
- `data-storage-room` (Room/DAO/DB changes in persistence/storage layer)
- `mvi-end-to-end-loop` (full MVI edit->review loop when you need a consistent wiring pass)

Also ask for:

- `featureName` (e.g., `weather`)
- which files/paths changed (optional, but helps choose the exact reviewers)
- whether root integration changed (`settings.gradle.kts`, `composeApp/build.gradle.kts`, app DI,
  app navigation)

### 2) Select subagents (minimal set)

Map categories -> subagents:

- `ui-navigation-di` -> `subagent_type="ui-layer-decompose-mvi-navigation-di-reviewer"`
- `ui-mvi-state-wiring` -> `subagent_type="ui-domain-mvi-state-methods-reviewer"`
- `domain-resource-errors` -> `subagent_type="domain-layer-usecases-resource-reviewer"`
- `data-network-dispatchers` -> `subagent_type="data-network-layer-ktor-dispatchers-reviewer"`
- `data-repository-mappers` -> `subagent_type="data-layer-repository-mappers-reviewer"`
- `data-storage-room` -> `subagent_type="data-storage-layer-room-reviewer"`
- `mvi-end-to-end-loop` -> `subagent_type="mvi-edit-review-loop"`

If the user reports compile errors:

- still run the relevant reviewers based on the error location category
- additionally run `ui-domain-mvi-state-methods-reviewer` when the errors mention
  `dispatch/forward`, Store types, or State/Label/Intent mismatches

### 3) Execute review/edit cycles

For each selected subagent:

- run in reviewer mode (enforce only the relevant constraints)
- if the subagent reports concrete issues, apply minimal fixes and re-run the same subagents until
  clean (or until the user stops)

### 4) Output

Summarize:

- chosen categories + subagents that were actually run
- the main issues fixed (if any)
- any remaining risks/testing gaps

## 5) Finish with repo verification

Before declaring the feature clean:

- invoke `.cursor/skills/repo-verification-orchestrator/SKILL.md`
- run targeted verification for the touched feature modules
- if root integration changed, also run the structure check script and mention those files
  explicitly in the report

