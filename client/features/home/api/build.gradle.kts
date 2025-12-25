plugins {
    id("multiplatform-convention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.client.navigation.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
