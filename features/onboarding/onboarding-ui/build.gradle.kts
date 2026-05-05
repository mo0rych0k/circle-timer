plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.app.composeMultiplatform)
}

kotlin {
    android {
        namespace = "com.circle.timer.features.onboarding.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.common.coreDi)
            implementation(libs.decompose)
            implementation(libs.compose.material3)
        }
    }
}
