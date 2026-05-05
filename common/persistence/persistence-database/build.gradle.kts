plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "com.circle.timer.common.core.persistence.db"

    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.common.coreDi)
            implementation(projects.common.coreThreading)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.kotlinx.serialization)
        }
        androidMain.dependencies {
            implementation(libs.androidx.room.sqlite.wrapper)
        }
    }
}

dependencies {
    ksp(libs.androidx.room.compiler)
}


room {
    schemaDirectory("$projectDir/schemas")
}
