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
        jvmMain {
            dependencies {
                implementation(libs.ktor.serverCore)
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata",projects.network.clientProcessor)
    add("kspJvm",projects.network.serverProcessor)
}
