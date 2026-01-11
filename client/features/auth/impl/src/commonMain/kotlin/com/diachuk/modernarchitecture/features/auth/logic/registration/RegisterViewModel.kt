package com.diachuk.modernarchitecture.features.auth.logic.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diachuk.architecture.network.api.auth.RegisterRequest
import com.diachuk.modernarchitecture.features.home.api.HomeDestination
import com.diachuk.modernarchitecture.navigaion.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class RegisterViewModel(
    private val navigator: Navigator,
    private val registerUseCase: RegisterUseCase
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
            val request = RegisterRequest(
                name = event.name,
                email = event.email,
                password = event.password
            )
            
            when (val result = registerUseCase.execute(request)) {
                RegisterResult.Success -> navigator.replaceAll(HomeDestination)
                is RegisterResult.Error -> _state.update { it.copy(error = result.message) }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
