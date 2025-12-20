package com.diachuk.modernarchitecture.features.auth.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.architecture.network.api.auth.LoginRequest
import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.modernarchitecture.features.auth.TokenStore
import com.diachuk.modernarchitecture.features.auth.api.RegisterDestination
import com.diachuk.modernarchitecture.navigaion.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

sealed interface LoginEvent {
    data class Login(val email: String, val password: String) : LoginEvent
    data object Register : LoginEvent
}

@KoinViewModel
class LoginViewModel(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
    private val navigator: Navigator
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> login(event)
            LoginEvent.Register -> navigator.navigate(RegisterDestination)
        }
    }

    private fun login(event: LoginEvent.Login) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = authApi.login(LoginRequest(event.email, event.password))
                tokenStore.saveToken(JwtEntity.UserToken::class, response.token)
                // Navigate to home or back? For now we just stay or maybe go back if authorized.
                // Assuming success means we can proceed.
                // navigator.back() // or similar
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
}