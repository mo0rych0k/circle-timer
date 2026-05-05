plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.circle.timer.utils.logging.impl"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
            implementation(projects.common.coreDi)
            implementation(projects.common.utils.logging)
        }
    }
}
