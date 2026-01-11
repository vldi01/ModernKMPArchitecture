package com.diachuk.modernarchitecture.features.auth.logic.login

sealed interface LoginEvent {
    data class Login(val email: String, val password: String) : LoginEvent
}
