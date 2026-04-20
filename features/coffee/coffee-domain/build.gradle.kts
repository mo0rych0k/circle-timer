plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "io.pylyp.coffee.domain"

    }

    sourceSets {
        commonMain.dependencies {
            api(projects.common.coreDomain)
        }
    }
}
