package com.diachuk.architecture.network.api.user

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Long, val name: String, val email: String)