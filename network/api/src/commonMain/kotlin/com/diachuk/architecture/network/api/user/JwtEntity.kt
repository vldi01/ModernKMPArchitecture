package com.diachuk.architecture.network.api.user

import com.diachuk.architecture.network.core.JwtType
import kotlinx.serialization.Serializable

@Serializable
sealed interface JwtEntity {
    @Serializable
    @JwtType
    data class UserToken(val userId: String) : JwtEntity

    @Serializable
    @JwtType
    data class RegistrationToken(val email: String) : JwtEntity

    @Serializable
    @JwtType
    data class New(
        val email: String,
        val password: String,
        val name: String,
        val surname: String,
        val skills: List<String>,
        val hobbies: String
    ) : JwtEntity


    @Serializable
    @JwtType
    object Empty : JwtEntity
}
