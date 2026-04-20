plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "io.pylyp.utils.logging"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.common.coreDi)
        }
    }
}
