import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.app.composeMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

val appPackageName = "com.circle.timer.composeapp"

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
            implementation(projects.common.utils.logging)
            implementation(projects.common.utils.loggingImpl)
            implementation(projects.features.onboarding.onboardingUi)
            implementation(projects.features.timer.timerData)
            implementation(projects.features.timer.timerDomain)
            implementation(projects.features.timer.timerUi)


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
