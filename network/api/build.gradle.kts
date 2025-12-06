plugins {
    id("multiplatform-convention")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.network.core)
                implementation(libs.ktor.clientCore)
            }
        }
    }
}

dependencies {
    addAllKsp(projects.network.clientProcessor)
}
