package com.diachuk.modernarchitecture.features.auth.logic.registration

sealed interface RegisterEvent {
    data class Register(val name: String, val email: String, val password: String) : RegisterEvent
}