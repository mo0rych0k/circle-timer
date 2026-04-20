plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "io.pylyp.common.core.domain"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.common.coreDi)
            api(projects.common.coreFoundation)
        }
    }
}
