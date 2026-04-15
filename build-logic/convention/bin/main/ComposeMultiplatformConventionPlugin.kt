import io.pylyp.build.logic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class ComposeMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("composeMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("composeCompiler").get().get().pluginId)
        }
    }
}