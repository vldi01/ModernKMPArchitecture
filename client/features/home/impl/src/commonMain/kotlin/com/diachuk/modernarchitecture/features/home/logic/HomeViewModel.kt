package com.diachuk.modernarchitecture.features.home.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.architecture.network.api.user.UserApi
import com.diachuk.architecture.network.core.safeApiCall
import com.diachuk.modernarchitecture.features.auth.api.LoginDestination
import com.diachuk.modernarchitecture.features.auth.api.TokenStore
import com.diachuk.modernarchitecture.navigaion.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val userApi: UserApi,
    private val tokenStore: TokenStore,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        loadUsers()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.OnLogoutClick -> logout()
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            safeApiCall { userApi.searchUsers(q = "", limit = null) }
                .onSuccess { response ->
                    _state.update { it.copy(users = response) }
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message ?: "Unknown error") }
                }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            tokenStore.clearToken(JwtEntity.UserToken::class)
            navigator.replaceAll(LoginDestination)
        }
    }
}
