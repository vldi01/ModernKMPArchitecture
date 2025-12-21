package com.diachuk.modernarchitecture.features.auth.logic.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.architecture.network.api.auth.AuthResponse
import com.diachuk.architecture.network.api.auth.RegisterRequest
import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.architecture.network.core.safeApiCall
import com.diachuk.modernarchitecture.features.auth.api.TokenStore
import com.diachuk.modernarchitecture.navigaion.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class RegisterViewModel(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.Register -> register(event)
        }
    }

    private fun register(event: RegisterEvent.Register) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            safeApiCall {
                authApi.register(
                    RegisterRequest(
                        name = event.name,
                        email = event.email,
                        password = event.password
                    )
                )
            }
                .onSuccess { response ->
                    if (response is AuthResponse.Authorized) {
                        tokenStore.saveToken(JwtEntity.UserToken::class, response.token)
                    }
                    // TODO: navigate to home
                }
                .onFailure { e ->
                    println(e)
                    _state.update { it.copy(error = e.message ?: "Registration failed") }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }
}