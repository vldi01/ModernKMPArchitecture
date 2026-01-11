package com.diachuk.architecture.network.api.user

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(val name: String, val email: String)