plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.app.composeMultiplatform)
}

val modulePackage = "com.circle.timer.common.resources"

kotlin {
    android {
        namespace = modulePackage

        androidResources.enable = true
    }

    jvmToolchain(21)

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.components.resources)
            implementation(libs.compose.runtime)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.circle.timer.common.resources"
    generateResClass = always
}
