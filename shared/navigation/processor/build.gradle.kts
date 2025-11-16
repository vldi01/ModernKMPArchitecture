plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(projects.shared.navigation.core)
    implementation(libs.ksp.symbol.processing.api)
    implementation(libs.kotlinpoet.core)
    implementation(libs.kotlinpoet.ksp)

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    implementation(libs.androidx.navigation3.ui)
}

