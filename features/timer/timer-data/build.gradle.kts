plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.circle.timer.features.timer.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.timer.timerDomain)
            implementation(projects.common.coreDi)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.noarg)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
