package com.diachuk.architecture.network.api.auth

import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class AuthNetworkDiModule {
    @Single
    fun provideAuthApi(ktorfit: Ktorfit): AuthApi = ktorfit.createAuthApi()
}