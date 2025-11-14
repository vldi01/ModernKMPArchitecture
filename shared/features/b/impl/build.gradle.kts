plugins {
    id("multiplatform-convention")
    id("compose-convention")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.shared.features.b.api)
                implementation(projects.shared.navigation)
            }
        }
    }
}