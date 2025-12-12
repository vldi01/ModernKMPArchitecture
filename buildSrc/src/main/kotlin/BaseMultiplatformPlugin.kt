import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class BaseMultiplatformPlugin : Plugin<Project> {
    lateinit var libs: LibrariesForLibs

    override fun apply(project: Project) {
        with(project) {
            libs = extensions.getByType(LibrariesForLibs::class.java)
            apply {
                plugin(libs.plugins.kotlin.multiplatform.get().pluginId)
                plugin(libs.plugins.androidLibrary.get().pluginId)
                plugin(libs.plugins.ksp.get().pluginId)
                plugin(libs.plugins.kotlin.serialization.get().pluginId)
            }
            setupKotlin()
            setupAndroid()
            setupKoin()
        }
    }

    private fun Project.setupAndroid() {
        androidExtension {
            compileSdk = libs.versions.android.compileSdk.get().toInt()
            sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
            defaultConfig {
                minSdk = libs.versions.android.minSdk.get().toInt()
            }
            namespace = "com.cheershunt" + path.replace(":", ".")

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
        }
    }

    private fun Project.setupKotlin() {
        kotlinExtension {
            androidTarget {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }

            listOf(
                iosArm64(),
                iosSimulatorArm64()
            ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = project.name
                    isStatic = true
                }
            }

            jvm()

            js {
                browser()
                binaries.executable()
            }

            @OptIn(ExperimentalWasmDsl::class)
            wasmJs {
                browser()
                binaries.executable()
            }

            sourceSets.commonMain.dependencies {
                implementation(libs.kotlinx.serialization)
            }
            sourceSets.commonTest.dependencies {
                implementation(kotlin("test"))
            }

            sourceSets.commonMain {
                kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            }

            project.tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }
                .configureEach {
                    dependsOn("kspCommonMainKotlinMetadata")
                }
        }
    }

    private fun Project.setupKoin() {
        kotlinExtension {
            sourceSets.commonMain {
                dependencies {
                    implementation(project.dependencies.platform(libs.koin.bom))
                    implementation(libs.koin.core)
                    implementation(libs.koin.annotations)
                }
            }
        }

        dependencies {
            addAllKsp(libs.koin.ksp.compiler)
        }
    }
}