package com.diachuk.modernarchitecture.features.a

import androidx.lifecycle.ViewModel
import com.diachuk.architecture.network.api.user.UserApiClient
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class AViewModel(
    @InjectedParam
    private val destination: DestinationA
) : ViewModel() {
    val test: UserApiClient? = null

    init {
        println("[AViewModel] destination = ${destination}")

    }
}