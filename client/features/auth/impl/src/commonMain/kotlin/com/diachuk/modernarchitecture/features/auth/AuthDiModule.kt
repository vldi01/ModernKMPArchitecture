package com.diachuk.modernarchitecture.features.auth

import com.diachuk.modernarchitecture.features.auth.api.SplashDestination
import com.diachuk.modernarchitecture.navigaion.StartDestination
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan
@Configuration
object AuthDiModule {
    @Single
    fun provideStartDestination(): StartDestination = SplashDestination
}
