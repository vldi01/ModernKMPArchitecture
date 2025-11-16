import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.compose.ComposePlugin

class ComposePlugin : Plugin<Project> {
    lateinit var libs: LibrariesForLibs

    override fun apply(project: Project) {
        with(project) {
            libs = extensions.getByType(LibrariesForLibs::class.java)
            apply {
                plugin(libs.plugins.composeCompiler.get().pluginId)
                plugin(libs.plugins.composeMultiplatform.get().pluginId)
                plugin(libs.plugins.composeHotReload.get().pluginId)
            }

            kotlinExtension {
                sourceSets {
                    commonMain.dependencies {
                        val compose = ComposePlugin.Dependencies(project)
                        implementation(compose.runtime)
                        implementation(compose.foundation)
                        implementation(compose.material3)
                        implementation(compose.ui)
                        implementation(compose.components.resources)
                        implementation(compose.components.uiToolingPreview)
                        implementation(libs.koin.compose.viewmodel)
                        implementation(libs.androidx.navigation3.runtime)
                    }
                }
            }
        }
    }
}