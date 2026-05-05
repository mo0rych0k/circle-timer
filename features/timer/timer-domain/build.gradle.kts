plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.circle.timer.features.timer.domain"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.common.coreDomain)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
