import com.android.build.api.dsl.ApplicationExtension
import io.pylyp.buildgradle.logic.Constants

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.app.composeMultiplatform)
}

project.extensions.configure<ApplicationExtension> {
    namespace = "com.pylyp.sample"
    compileSdk = Constants.ANDROID_COMPILE_SDK

    defaultConfig {
        applicationId = "io.pylyp.everydaytask"
        minSdk = Constants.ANDROID_MIN_SDK
        targetSdk = Constants.ANDROID_TARGET_SDK
        versionCode = Constants.APP_BUILD
        versionName = Constants.APP_VERSION
    }

    buildFeatures {
        aidl = false
        viewBinding = false
        prefab = false
        shaders = false
        buildConfig = true
        resValues = true
        compose = true
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(projects.common.appInfo)
    implementation(projects.composeApp)
    implementation(projects.common.coreDi)
    implementation(projects.common.uikit)
    implementation(projects.common.coreNavigation)
    implementation(libs.koin.android)
    implementation(libs.androidx.activity.compose)
}
