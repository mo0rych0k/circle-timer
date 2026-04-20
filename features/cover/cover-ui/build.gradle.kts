plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.app.composeMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "io.pylyp.cover.ui"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization)
            implementation(projects.common.uikit)
            implementation(projects.common.coreNavigation)
            implementation(projects.common.coreDi)
        }
    }
}
