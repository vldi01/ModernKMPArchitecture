package com.diachuk.architecture.network.api.user

import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

// TODO: remove after koin fixes multiround processing
@Module
@Configuration
@ComponentScan
class NetworkApiDiModule {
    @Single
    fun provideUserApi(ktorfit: Ktorfit): UserApi = ktorfit.createUserApi()
}