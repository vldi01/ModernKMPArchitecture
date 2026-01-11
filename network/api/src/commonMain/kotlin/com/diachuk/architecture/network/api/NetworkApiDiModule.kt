package com.diachuk.architecture.network.api

import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.architecture.network.api.auth.createAuthApi
import com.diachuk.architecture.network.api.user.UserApi
import com.diachuk.architecture.network.api.user.createUserApi
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@Configuration
@ComponentScan
class NetworkApiDiModule {
    @Single
    fun provideUserApi(ktorfit: Ktorfit): UserApi = ktorfit.createUserApi()

    @Single
    fun provideAuthApi(ktorfit: Ktorfit): AuthApi = ktorfit.createAuthApi()
}