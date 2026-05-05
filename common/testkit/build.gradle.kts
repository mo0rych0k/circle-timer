plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.circle.timer.common.testkit"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization)
            implementation(projects.common.coreDi)
            api(projects.common.coreThreadingTest)
            api(libs.kotlinx.coroutines.test)
            api(libs.turbine)
        }
    }
}

