plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.app.composeMultiplatform)
}

kotlin {
    android {
        namespace = "com.circle.timer.common.core.ui"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.components.resources)

            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.ui)
            api(libs.compose.runtime)
            api(projects.common.uikit)
            api(projects.common.resources)
            api(projects.common.coreDi)
            api(projects.common.coreFoundation)
            api(projects.common.coreNavigation)
            api(projects.common.coreThreading)

            androidMain.dependencies {
                api(libs.compose.ui)
            }
            iosMain.dependencies {
                api(libs.compose.ui)
            }
            jvmMain.dependencies {
                api(libs.compose.ui)
            }
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
