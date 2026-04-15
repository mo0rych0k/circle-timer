# KMP Decompose MVI Template

🚀 This is a modern **Opinionated Kotlin Multiplatform** template for rapid development of scalable
applications. It combines the power of Decompose for navigation and MVIKotlin for a clean
architecture.

## 📚 Core Libraries

- **[Decompose](https://github.com/arkivanov/Decompose)** — Navigation and lifecycle management.
- **[MVIKotlin](https://github.com/arkivanov/MVIKotlin)** — Implementation of the Model-View-Intent
  architecture.
- **[Koin](https://insert-koin.io/)** — Pragmatic lightweight DI framework.
- **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)** — Declarative UI
  for Android, iOS, Desktop, and Web.
- **[Room (KMP)](https://developer.android.com/kotlin/multiplatform/room)** — Cross-platform
  database.
- **[Ktor](https://ktor.io/)** — HTTP client for multiplatform networking.
- **[Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)** — Multiplatform JSON
  parsing.
- **[Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)** — Reactive
  programming and asynchrony.

## 🤖 Smart Development (Agent-Ready)

This project is built with modern AI assistants in mind. It includes a set of tools that allow AI to
understand the project structure and automate routine tasks.

### 🛠 Skills & Subagents

Detailed instructions for AI agents located in `.agents/skills/`:

- 🔄 `template-setup` — Quick start of a new project and package renaming.
- 🏗 `team-builder` — Tool for coordinating a team of subagents.
- 📦 `kotlin-room-patterns` — Database interaction patterns.
- 🌐 `kotlin-ktor-patterns` — Network interaction and API patterns.
- 🧪 `kotlin-testing` — Testing standards (Kotest, MockK).
- 🔒 `security-scan` — Configuration vulnerability scanning.
- 🧐 `verification-loop` — Automated result verification system.

### 📋 Rules

The `.cursor/rules` directory contains rules (.mdc) that guide AI code generation:

- **Architecture**: Deep understanding of Decompose and MVI.
- **Layers**: Strict separation of Domain, Data, and UI layers.
- **Features**: Standardized approach for creating new feature modules.

## ⚙️ Initial Setup

The template includes a powerful Python script for automatic configuration. This allows you to
instantly change the project name and base package in every file.

### `setup_project.py` Features:

- 🔄 **Rename Project**: Updates the name in `settings.gradle.kts`.
- 📦 **Refactor Packages**: Recursively replaces `io.pylyp` with your package and moves files
  accordingly.
- 🧹 **Clean Samples**: Removes demo modules (`coffee`, `weather`, `cover`).
- ✅ **Database & Resources**: Cleans the database and resources from sample code.

### Usage Example:

```bash
python3 .agents/skills/template-setup/scripts/setup_project.py \
  --new-name "My Super App" \
  --new-package "com.company.myapp"
```

> [!IMPORTANT]
> After the script completes, make sure to perform **Sync Project with Gradle Files** in your IDE (
> Android Studio or IntelliJ IDEA).
