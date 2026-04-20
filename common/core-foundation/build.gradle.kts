plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "io.pylyp.common.core.foundation"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
