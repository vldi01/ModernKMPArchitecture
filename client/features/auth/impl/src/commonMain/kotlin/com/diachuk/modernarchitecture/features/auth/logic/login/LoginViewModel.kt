package com.diachuk.modernarchitecture.features.auth.logic.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.modernarchitecture.features.auth.api.TokenStore
import com.diachuk.modernarchitecture.features.home.api.HomeDestination
import com.diachuk.modernarchitecture.navigaion.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LoginViewModel(
    private val navigator: Navigator,
    private val loginUseCase: LoginUseCase
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
            when (val loginResult = loginUseCase.execute(event.email, event.password)) {
                LoginResult.Success -> {
                    navigator.replaceAll(HomeDestination)
                }

                is LoginResult.Error -> _state.update { it.copy(error = loginResult.message) }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
