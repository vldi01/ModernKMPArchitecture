package com.diachuk.modernarchitecture.features.auth.logic.login

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null
)