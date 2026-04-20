plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "io.pylyp.weather.data"

    }

    sourceSets {
        val mobileMain by creating {
            dependsOn(commonMain.get())
        }

        androidMain.get().dependsOn(mobileMain)
        iosArm64Main.get().dependsOn(mobileMain)
        iosSimulatorArm64Main.get().dependsOn(mobileMain)

        commonMain.dependencies {
            implementation(projects.common.coreDi)
            implementation(projects.common.coreThreading)
            implementation(projects.common.persistence.persistenceDatabase)
            implementation(projects.features.weather.weatherDataNetwork)
            implementation(projects.features.weather.weatherDomain)
            implementation(libs.compass.geolocation)
        }

        mobileMain.dependencies {
            implementation(libs.compass.geolocation.mobile)
            implementation(libs.compass.permissions.mobile)
            implementation(libs.compass.geocoder)
            implementation(libs.compass.geocoder.mobile)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.core.ktx)
        }
    }
}
