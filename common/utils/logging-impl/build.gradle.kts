plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "io.pylyp.utils.logging.impl"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
            implementation(projects.common.coreDi)
            implementation(projects.common.utils.logging)
        }
    }
}
