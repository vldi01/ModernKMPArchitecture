plugins {
    id("multiplatform-convention")
    alias(libs.plugins.ktorfit)
}

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.network.core)
                implementation(libs.ktor.clientCore)
                implementation(libs.ktorfit.lib)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.ktor.serverCore)
                implementation(libs.ktor.serverAuth)
                implementation(libs.ktor.serverAuthJwt)
            }
        }
    }
}

dependencies {
    add("kspJvm", projects.network.serverProcessor)
}
