plugins {
    id("multiplatform-convention")
    alias(libs.plugins.ktorfit)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.network.core)
                implementation(libs.ktor.clientCore)
                implementation(libs.ktorfit.lib)
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
    add("kspJvm",projects.network.serverProcessor)
}
