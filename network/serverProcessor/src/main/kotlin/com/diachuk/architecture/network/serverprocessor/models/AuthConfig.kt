package com.diachuk.architecture.network.serverprocessor.models

import com.squareup.kotlinpoet.ClassName

sealed class AuthConfig {
    data object None : AuthConfig()
    data class Required(val securityName: String, val principalClass: ClassName) : AuthConfig()
}