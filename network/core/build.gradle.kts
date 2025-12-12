plugins {
    id("multiplatform-convention")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ktor.clientCore)
                implementation(libs.ktor.clientContentNegotiation)
                implementation(libs.ktor.serializationKotlinxJson)
                implementation(libs.ktor.clientLogging)
                implementation(libs.ktorfit.lib)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.ktor.clientOkHttp)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.ktor.clientDarwin)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.ktor.clientOkHttp)
            }
        }
    }
}