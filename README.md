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

## 🧱 Build Configuration (Important)

Build and app version values are now centralized in build logic constants instead of
`gradle/libs.versions.toml`.

- **Source of truth**:
  `build-logic/convention/src/main/kotlin/io/pylyp/buildgradle/logic/Constants.kt`
- **What is stored there**:
    - Android SDK versions (`compileSdk`, `minSdk`, `targetSdk`)
    - App version metadata (`APP_VERSION`, `APP_BUILD`)
    - iOS minimum deployment version (`IOS_MIN_VERSION`)
- **Version catalog scope**: `gradle/libs.versions.toml` is focused on dependency/plugin versions.

### iOS Version Sync

The `updateIosVersion` Gradle task now reads app version/build from `Constants` and updates:

- `iosApp/iosApp/Info.plist`
- `iosApp/iosApp.xcodeproj/project.pbxproj`

Run:

```bash
./gradlew updateIosVersion
```
