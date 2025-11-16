plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.gradle.plugin)
    implementation(libs.kotlin.ksp.gradle.plugin)
    implementation(libs.android.tools.build.gradle)
}

gradlePlugin {
    plugins {
        register("multiplatform-convention") {
            id = "multiplatform-convention"
            implementationClass = "BaseMultiplatformPlugin"
        }
        register("compose-convention") {
            id = "compose-convention"
            implementationClass = "ComposePlugin"
        }
    }
}