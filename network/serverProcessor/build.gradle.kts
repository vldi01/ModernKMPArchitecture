plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.ksp.symbol.processing.api)
    implementation(libs.kotlinpoet.core)
    implementation(libs.kotlinpoet.ksp)
    implementation(project(":network:core"))
    implementation(libs.ktor.serverCore)
    implementation(libs.ktorfit.lib)
}
