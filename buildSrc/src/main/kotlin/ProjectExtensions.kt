import com.android.build.gradle.LibraryExtension
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
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

