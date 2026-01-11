plugins {
    id("multiplatform-convention")
    id("compose-convention")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.client.features.user.api)
                implementation(libs.androidx.room.runtime)
            }
        }
    }
}
