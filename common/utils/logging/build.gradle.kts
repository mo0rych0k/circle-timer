plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.circle.timer.utils.logging"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.common.coreDi)
        }
    }
}
