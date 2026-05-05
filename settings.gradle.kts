rootProject.name = "circle-timer"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
    }
    @Suppress("UnstableApiUsage")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":composeApp")
include(":server")

include(
    ":common:core-di",
    ":common:core-navigation",
    ":common:core-network",
    ":common:core-threading",
    ":common:core-threading-test",
    ":common:core-domain",
    ":common:core-ui",
    ":common:resources",
    ":common:testkit",
    ":common:uikit",
    ":common:utils:logging",
    ":common:utils:logging-impl",
    ":common:app-info",
    ":common:persistence:persistence-database",
)

include(":features:onboarding:onboarding-ui")

include(
    ":features:timer:timer-data",
    ":features:timer:timer-domain",
    ":features:timer:timer-ui",
)


include(":common:core-foundation")
