import java.net.URI

rootProject.name = "ModernArchitecture"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven { url = URI.create("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")
include(":server")
include(":network:api")
include(":network:core")
include(":network:serverProcessor")
include(":client:core")
include(":client:network")
include(":client:database")
include(":client:design")
include(":client:navigation:core")
include(":client:navigation:processor")
include(":client:features:a:api")
include(":client:features:a:impl")
include(":client:features:b:api")
include(":client:features:b:impl")