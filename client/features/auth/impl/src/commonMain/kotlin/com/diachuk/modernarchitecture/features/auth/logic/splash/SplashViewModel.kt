package com.diachuk.modernarchitecture.features.auth.logic.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.modernarchitecture.features.auth.api.LoginDestination
import com.diachuk.modernarchitecture.features.auth.api.TokenStore
import com.diachuk.modernarchitecture.features.home.api.HomeDestination
import com.diachuk.modernarchitecture.navigaion.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SplashViewModel(
    private val tokenStore: TokenStore,
    private val navigator: Navigator
) : ViewModel() {

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
             delay(200)
            
            val token = tokenStore.getToken(JwtEntity.UserToken::class)
            if (token != null) {
                navigator.replaceAll(HomeDestination)
            } else {
                navigator.replaceAll(LoginDestination)
            }
        }
    }
}
