plugins {
    id("multiplatform-convention")
    id("compose-convention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.client.features.home.api)
            implementation(projects.client.database)
            implementation(projects.client.navigation.core)
            implementation(projects.network.api)
            implementation(projects.network.core)
            implementation(projects.client.resources)
            implementation(libs.ktorfit.lib)
            implementation(libs.androidx.room.runtime)
            implementation(projects.client.features.auth.api)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(compose.uiTooling)
        }
    }
}
