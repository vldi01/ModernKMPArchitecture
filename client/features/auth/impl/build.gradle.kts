plugins {
    id("multiplatform-convention")
    id("compose-convention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.client.features.auth.api)
            implementation(projects.client.database)
            implementation(projects.client.navigation.core)
            implementation(projects.network.api)
            implementation(projects.network.core)
            implementation(libs.ktorfit.lib)
            implementation(libs.androidx.room.runtime)
        }
        androidMain.dependencies {
            implementation(compose.uiTooling)
        }
    }
}
