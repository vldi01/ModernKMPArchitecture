import com.android.build.gradle.LibraryExtension
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun Project.androidExtension(block: LibraryExtension.() -> Unit) {
    extensions.getByType(LibraryExtension::class.java).block()
}

fun Project.kotlinExtension(block: KotlinMultiplatformExtension.() -> Unit) {
    extensions.getByType(KotlinMultiplatformExtension::class.java).block()
}

fun Project.kspExtension(block: KspExtension.() -> Unit) {
    extensions.getByType(KspExtension::class.java).block()
}

fun DependencyHandlerScope.addAllKsp(dependencyNotation: Any) {
    add("kspCommonMainMetadata", dependencyNotation)
    add("kspAndroid", dependencyNotation)
    add("kspIosArm64", dependencyNotation)
    add("kspIosSimulatorArm64", dependencyNotation)
    add("kspJvm", dependencyNotation)
    add("kspJs", dependencyNotation)
    add("kspWasmJs", dependencyNotation)
}