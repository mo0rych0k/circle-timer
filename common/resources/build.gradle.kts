plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.app.composeMultiplatform)
}

val modulePackage = "io.pylyp.common.resources"

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
    packageOfResClass = "io.pylyp.common.resources"
    generateResClass = always
}
