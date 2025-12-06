package com.diachuk.modernarchitecture.features.a

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diachuk.architecture.network.api.user.UserApi
import com.diachuk.architecture.network.core.safeApiCall
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class AViewModel(
    @InjectedParam
    private val destination: DestinationA,
    private val userApi: UserApi
) : ViewModel() {
    init {
        println("[AViewModel] destination = ${destination}")

        viewModelScope.launch {
            safeApiCall { userApi.getUser(1) }
                .onSuccess {
                    println("[AViewModel] user = ${it}")
                }
                .onFailure {
                    println("[AViewModel] error = ${it}")
                }
        }
    }
}