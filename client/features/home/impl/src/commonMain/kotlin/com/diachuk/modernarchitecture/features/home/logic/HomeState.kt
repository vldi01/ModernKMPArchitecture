package com.diachuk.modernarchitecture.features.home.logic

import com.diachuk.architecture.network.api.user.User

data class HomeState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
