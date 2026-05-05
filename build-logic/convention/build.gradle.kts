plugins {
    `kotlin-dsl`
}

group = "com.circle.timer.buildgradle.logic"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    compileOnly(libs.detekt.gradle.plugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "com.circle.timer.kotlin.multiplatform"
            implementationClass = "KotlinMultiplatformConventionPlugin"
        }
        register("composeMultiplatform") {
            id = "com.circle.timer.kotlin.composeMultiplatform"
            implementationClass = "ComposeMultiplatformConventionPlugin"
        }
        register("iosVersionUpdate") {
            id = "com.circle.timer.ios.versionUpdate"
            implementationClass = "com.circle.timer.buildgradle.logic.IosVersionUpdatePlugin"
        }
    }
}
