package com.diachuk.architecture.network.api.auth

import com.diachuk.architecture.network.core.ErrorResponse
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthResponse {
    @Serializable
    data class Authorized(val token: String) : AuthResponse
    @Serializable
    data object InvalidCredentials : AuthResponse, ErrorResponse
    @Serializable
    data object UserAlreadyExists : AuthResponse, ErrorResponse
}
