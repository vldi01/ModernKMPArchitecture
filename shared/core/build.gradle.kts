plugins {
    id("multiplatform-convention")
    id("compose-convention")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                // Needed for DI
                implementation(projects.shared.features.a.impl)
                implementation(projects.shared.features.b.impl)

                implementation(projects.shared.navigation)
            }
        }
    }
}