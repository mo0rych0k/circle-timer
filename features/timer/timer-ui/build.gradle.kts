plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.app.composeMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "com.circle.timer.features.timer.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.common.coreDi)
            implementation(projects.common.coreNavigation)
            implementation(projects.common.coreThreading)
            implementation(projects.features.timer.timerDomain)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.decompose)
            implementation(libs.decompose.compose)
            implementation(libs.compose.material3)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
