plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.circle.timer.common.core.di"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.common.appInfo)
            api(libs.koin.core)
            api(libs.koin.compose)
        }

        commonTest.dependencies {
            implementation(projects.common.coreThreadingTest)
        }
    }
}
