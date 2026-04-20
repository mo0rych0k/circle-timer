plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.app.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    android {
        namespace = "io.pylyp.core.navigation"

    }

    sourceSets {
        commonMain.dependencies {
            api(libs.decompose)
            api(libs.decompose.compose)
            api(libs.essenty.lifecycle.coroutines)
            api(libs.mvikotlin)
            api(libs.mvikotlin.main)
            api(libs.mvikotlin.extensions.coroutines)
            api(libs.kotlinx.coroutines.core)
            api(projects.common.coreThreading)

            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
        }
    }
}
