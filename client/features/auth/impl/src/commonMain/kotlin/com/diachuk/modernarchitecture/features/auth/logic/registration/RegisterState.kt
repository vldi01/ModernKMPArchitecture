package com.diachuk.modernarchitecture.features.auth.logic.registration

data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null
)