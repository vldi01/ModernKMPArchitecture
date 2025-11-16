package com.diachuk.modernarchitecture.features.a

import com.diachuk.modernarchitecture.navigaion.StartDestination
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@Configuration
@ComponentScan
class DIModuleA {
    @Single
    fun provideStartDestination(): StartDestination = DestinationA
}