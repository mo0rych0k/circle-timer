plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.circle.timer.common.core.domain"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.common.coreDi)
            api(projects.common.coreFoundation)
        }
    }
}
