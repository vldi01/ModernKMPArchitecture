package com.diachuk.architecture.network.core

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@Configuration
@ComponentScan
class NetworkCoreDiModule {

    @Single
    fun provideKtorfit(clientBuilder: ClientBuilder) = clientBuilder.buildKtorfit()
}