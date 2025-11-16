package com.diachuk.modernarchitecture.features.a

import androidx.lifecycle.ViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class AViewModel(
    @InjectedParam
    private val destination: DestinationA
) : ViewModel() {
    init {
        println("[AViewModel] destination = ${destination}")
    }
}