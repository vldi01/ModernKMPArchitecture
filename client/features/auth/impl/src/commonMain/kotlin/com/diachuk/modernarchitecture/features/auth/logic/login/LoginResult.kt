package com.diachuk.modernarchitecture.features.auth.logic.login

sealed interface LoginResult {
    data object Success : LoginResult
    data class Error(val message: String) : LoginResult
}
