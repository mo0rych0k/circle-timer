---
name: template-setup
description: Rename KMP project, change package name, and remove default feature modules. Use this skill when the user wants to start a new project from the kmp-decompose-mvi-template and needs to customize it.
---

# Template Setup Skill

This skill automates the initial setup of a new project created from the
`kmp-decompose-mvi-template`. It handles project renaming, package renaming, and cleanup of default
features including database and resources.

## Workflows

### Setup Project

To setup the project with a new name and package, run the `setup_project.py` script:

```bash
python3 .agents/skills/template-setup/scripts/setup_project.py --new-name "My New App" --new-package "com.example.app"
```

#### Arguments:

- `--new-name`: The new name for the project (updates `settings.gradle.kts`).
- `--new-package`: The new package prefix (e.g., `com.mycompany.myapp`).
- `--old-package`: (Optional) The old package prefix to replace (default: `io.pylyp`).

### What this skill does:

1. **Renames the project**: Updates `rootProject.name` in `settings.gradle.kts`.
2. **Renames packages**: Replaces all occurrences of the old package prefix with the new one and
   moves source directories accordingly.
3. **Deletes default features**: Removes `features/coffee`, `features/cover`, and `features/weather`
   modules.
4. **Cleans `settings.gradle.kts`**: Removes deleted modules from the include list.
5. **Cleans the database**: Removes feature-specific entities and DAOs from
   `common:persistence:persistence-database`.
6. **Cleans resources**: Removes feature-specific drawables and resets `strings.xml`.

## Best Practices

- Run this skill immediately after cloning or creating a new project from the template.
- Ensure you have a clean git state before running, as it performs many file operations.
- After running, perform a Gradle sync to apply changes.
