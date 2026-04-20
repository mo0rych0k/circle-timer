plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "io.pylyp.weather.domain"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization)
            api(projects.common.coreDomain)
        }
    }
}
