plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "io.pylyp.coffee.data.network"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization)
            implementation(projects.common.coreDi)
            implementation(projects.common.coreNetwork)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(projects.common.testkit)
        }
    }
}
