package com.diachuk.architecture.network.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String
)