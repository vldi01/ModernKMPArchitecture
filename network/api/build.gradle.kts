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
    add("kspCommonMainMetadata",projects.network.clientProcessor)
}
