plugins {
    id("multiplatform-convention")
    id("compose-convention")
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.diachuk.modernarchitecture.client.resources"
}
