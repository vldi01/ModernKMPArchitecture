package com.diachuk.modernarchitecture.features.auth.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.architecture.network.api.auth.RegisterRequest
import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.modernarchitecture.features.auth.TokenStore
import com.diachuk.modernarchitecture.navigaion.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class RegisterViewModel(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
    private val navigator: Navigator
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun onEmailChanged(value: String) {
        _email.value = value
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
    }

    fun onNameChanged(value: String) {
        _name.value = value
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response =
                    authApi.register(RegisterRequest(_email.value, _password.value, _name.value))
                tokenStore.saveToken(JwtEntity.UserToken::class, response.token)
                // Navigate back or to home
                navigator.popBack()
            } catch (e: Exception) {
                _error.value = e.message ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onBackClick() {
        navigator.popBack()
    }
}