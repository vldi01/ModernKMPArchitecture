package com.diachuk.modernarchitecture.features.auth.logic.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.architecture.network.api.auth.LoginRequest
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
class LoginViewModel(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> login(event)
        }
    }

    private fun login(event: LoginEvent.Login) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            safeApiCall { authApi.login(LoginRequest(event.email, event.password)) }
                .onSuccess { response ->
                    tokenStore.saveToken(JwtEntity.UserToken::class, response.token)
                    // TODO: navigate to home
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message ?: "Login failed") }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }
}