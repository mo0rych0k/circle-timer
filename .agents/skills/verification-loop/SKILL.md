---
name: verification-loop
description: "A repository-aware verification system for Gradle and Kotlin projects. Use after significant code changes, before PRs, and before claiming work is complete, especially in KMP or Ktor repositories."
origin: ECC
---

# Verification Loop Skill

A repository-aware verification system for Kotlin and Gradle codebases.

## When to Use

Invoke this skill:

- After completing a feature or significant code change
- Before creating a PR
- When you want to ensure quality gates pass
- After refactoring
- When module wiring, Gradle files, or app/server integration changed

## Verification Phases

### Phase 1: Choose the smallest valid verification scope

```bash
# Feature module examples
./gradlew :features:weather:weather-ui:check
./gradlew :features:weather:weather-data:check

# Shared module example
./gradlew :common:core-navigation:check

# Server example
./gradlew :server:test
```

Prefer targeted Gradle tasks over repo-wide builds when the change is localized.

If the change spans multiple modules or root wiring, include the nearest app/root consumer as well.

If verification fails, STOP and fix before continuing.

### Phase 2: Review integration points explicitly

```bash
git diff --stat
git diff --name-only
```

When module structure or navigation changed, explicitly inspect:

- `settings.gradle.kts`
- `composeApp/build.gradle.kts`
- `composeApp/.../di/AppModules.kt`
- `common/core-navigation`
- `composeApp/.../roating`

### Phase 3: Run repository structure checks when relevant

```bash
python3 ".cursor/skills/repo-verification-orchestrator/scripts/check_module_structure.py"
python3 ".cursor/skills/repo-verification-orchestrator/scripts/check_module_structure.py" --feature weather
```

Run this when modules are added, renamed, or wired into root app integration.

### Phase 4: Run tests appropriate to the touched module

```bash
./gradlew :features:coffee:coffee-data-network:allTests
./gradlew :server:test
```

Prefer module-local test tasks and expand only when the change affects shared code or app/root
wiring.

### Phase 5: Check diagnostics and diff quality

```bash
# IDE diagnostics are useful when available
# Then review the diff for unintended changes
git diff --stat
```

Report:

- Unintended changes
- Missing error handling
- Potential edge cases

## Output Format

After running all phases, produce a verification report:

```
VERIFICATION REPORT
==================

Build:     [PASS/FAIL]
Integration: [PASS/FAIL]
Structure: [PASS/FAIL]
Tests:     [PASS/FAIL]
Diff:      [X files changed]

Overall:   [READY/NOT READY] for PR

Issues to Fix:
1. ...
2. ...
```

## Continuous Mode

For long sessions, run verification every 15 minutes or after major changes:

```markdown
Set a mental checkpoint:

- After finishing a module-sized change
- After changing DI or root wiring
- Before moving to the next major area

Run: /verify
```

## Integration with Hooks

This skill complements PostToolUse hooks but provides deeper verification.
Hooks catch issues immediately; this skill provides comprehensive review.
