plugins {
    id("multiplatform-convention")
    id("compose-convention")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.client.features.a.api)
                implementation(projects.client.features.b.api)
                implementation(projects.client.navigation.core)
            }
        }
    }
}

dependencies {
    addAllKsp(projects.client.navigation.processor)
}
