plugins {
    alias(libs.plugins.app.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "io.pylyp.core.threading"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.common.coreDi)
            api(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies { }
        iosMain.dependencies { }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
        commonTest.dependencies { }
    }
}
