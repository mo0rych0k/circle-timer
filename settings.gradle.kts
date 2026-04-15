rootProject.name = "kmp-decompose-mvi-template"
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

include(":features:cover:cover-ui")

include(
    ":features:coffee:coffee-data",
    ":features:coffee:coffee-data-network",
    ":features:coffee:coffee-domain",
    ":features:coffee:coffee-ui",
)

include(
    ":features:weather:weather-data-network",
    ":features:weather:weather-data",
    ":features:weather:weather-domain",
    ":features:weather:weather-ui",
)


include(":common:core-foundation")
