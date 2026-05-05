package com.circle.timer.buildgradle.logic

import org.gradle.api.Project

/**
 * Extension to get Android SDK versions from version catalog
 */
@Suppress("unused")
fun Project.getAndroidSdkVersions(): AndroidSdkVersions {
    return AndroidSdkVersions(
        compileSdk = Constants.ANDROID_COMPILE_SDK,
        minSdk = Constants.ANDROID_MIN_SDK,
        targetSdk = Constants.ANDROID_TARGET_SDK,
    )
}

/**
 * Data class to hold Android SDK version information
 */
data class AndroidSdkVersions(
    val compileSdk: Int,
    val minSdk: Int,
    val targetSdk: Int,
)
