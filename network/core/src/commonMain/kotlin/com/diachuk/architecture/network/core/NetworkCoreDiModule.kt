package com.diachuk.architecture.network.core

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@Configuration
@ComponentScan
class NetworkCoreDiModule {
    @Single
    fun provideHttpClient(clientBuilder: ClientBuilder) = clientBuilder.buildMainHttpClient()
}