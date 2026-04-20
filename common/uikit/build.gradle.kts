plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.app.composeMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    android {
        namespace = "io.pylyp.common.uikit"


        androidResources.enable = true
    }

    jvmToolchain(21)

    sourceSets {
        androidMain.dependencies {
            // Tooling support - runtime only but transitive to consumer modules
            runtimeOnly(libs.compose.uiTooling)
        }
        commonMain.dependencies {
            api(libs.compose.components.resources)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.runtime)
            api(libs.compose.material.icons.extended)
            api(libs.compose.ui)
            api(libs.compose.uiToolingPreview)

            api(projects.common.coreFoundation)

            implementation(libs.coil.compose)
            implementation(libs.coil.ktor)
            implementation(libs.coil.svg)
        }
    }
}

