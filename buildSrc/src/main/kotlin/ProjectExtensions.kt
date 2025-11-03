import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.PluginDependenciesSpecScope
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun Project.androidExtension(block: LibraryExtension.() -> Unit) {
    extensions.getByType(LibraryExtension::class.java).block()
}

fun Project.kotlinExtension(block: KotlinMultiplatformExtension.() -> Unit) {
    extensions.getByType(KotlinMultiplatformExtension::class.java).block()
}

fun PluginDependenciesSpecScope.id(dependency: Provider<PluginDependency>) =
    id(dependency.get().pluginId)
