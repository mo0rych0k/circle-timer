plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "io.pylyp.coffee.data"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.common.coreDi)
            implementation(projects.features.coffee.coffeeDataNetwork)
            implementation(projects.common.persistence.persistenceDatabase)
            implementation(projects.features.coffee.coffeeDomain)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(projects.common.testkit)
        }
    }
}
