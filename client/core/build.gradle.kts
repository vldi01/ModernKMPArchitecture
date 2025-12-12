plugins {
    id("multiplatform-convention")
    id("compose-convention")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                // Needed for DI
                implementation(projects.client.features.a.impl)
                implementation(projects.client.features.b.impl)
                implementation(projects.client.features.user.impl)
                implementation(projects.client.database)

                implementation(projects.client.navigation.core)
                implementation(projects.network.api)
                implementation(projects.network.core)
            }
        }
    }
}