import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.circle.timer.buildgradle.logic.Constants
import com.circle.timer.buildgradle.logic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {

        val compileSdk = Constants.ANDROID_COMPILE_SDK
        val minSdk = Constants.ANDROID_MIN_SDK

        pluginManager.apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
        pluginManager.apply(libs.findPlugin("kotlinMultiplatformLibrary").get().get().pluginId)

        extensions.configure<KotlinMultiplatformExtension> {
            explicitApi = ExplicitApiMode.Strict

            jvm()
            iosArm64()
            iosSimulatorArm64()

            targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
                binaries.all {
                    freeCompilerArgs += listOf(
                        "-linker-option",
                        "-ios_version_min",
                        Constants.IOS_MIN_VERSION,
                    )
                }
            }



            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }

            sourceSets.all {
                languageSettings {
                    optIn("kotlin.ExperimentalStdlibApi")
                    optIn("kotlin.time.ExperimentalTime")
                    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                }
            }

            plugins.withId("com.android.kotlin.multiplatform.library") {
                targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
                    this.compileSdk = compileSdk
                    this.minSdk = minSdk
                }
            }
        }
    }
}
