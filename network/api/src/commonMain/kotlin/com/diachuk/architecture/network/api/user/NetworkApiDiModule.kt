package com.diachuk.architecture.network.api.user

import io.ktor.client.HttpClient
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
    fun provideUserApi(httpClient: HttpClient): UserApi = UserApiClient(httpClient)
}