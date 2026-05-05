plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.circle.timer.core.threading.test"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.common.coreDi)
            implementation(projects.common.coreThreading)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

