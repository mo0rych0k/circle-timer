plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.app.composeMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "io.pylyp.coffee.ui"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization)
            implementation(libs.coil.compose)
            implementation(libs.coil.ktor)

            implementation(projects.common.coreUi)
            implementation(projects.features.coffee.coffeeDomain)
        }
    }
}
