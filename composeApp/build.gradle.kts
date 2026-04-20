import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.app.composeMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

val appPackageName = "io.pylyp.sample.composeapp"

kotlin {
    android {
        namespace = appPackageName

    }

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", appPackageName)
        }
    }

    sourceSets {
        androidMain.dependencies {

        }
        commonMain.dependencies {
            /*common*/
            implementation(projects.common.appInfo)
            implementation(projects.common.uikit)
            implementation(projects.common.coreNavigation)
            implementation(projects.common.coreDi)
            implementation(projects.common.coreNetwork)
            implementation(projects.common.coreThreading)
            implementation(projects.common.persistence.persistenceDatabase)

            /*feature-cover*/
            implementation(projects.features.cover.coverUi)

            /*feature-coffee*/
            implementation(projects.features.coffee.coffeeData)
            implementation(projects.features.coffee.coffeeDataNetwork)
            implementation(projects.features.coffee.coffeeDomain)
            implementation(projects.features.coffee.coffeeUi)

            /*feature-weather*/
            implementation(projects.features.weather.weatherData)
            implementation(projects.features.weather.weatherDataNetwork)
            implementation(projects.features.weather.weatherDomain)
            implementation(projects.features.weather.weatherUi)


        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "${appPackageName}.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = appPackageName
            packageVersion = "1.0.0"
        }
    }
}

tasks.register("jvmRun") {
    group = "application"
    description = "Alias to run the Compose Desktop app (delegates to 'run')"
    dependsOn("run")
}
