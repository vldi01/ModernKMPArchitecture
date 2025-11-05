plugins {
    id("multiplatform-convention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.navigation)
        }
    }
}