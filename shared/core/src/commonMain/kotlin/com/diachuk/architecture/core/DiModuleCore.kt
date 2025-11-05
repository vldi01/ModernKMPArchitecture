package com.diachuk.architecture.core

import com.diachuk.modernarchitecture.features.a.DestinationA
import com.diachuk.modernarchitecture.navigaion.StartDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@Configuration
@ComponentScan
object DiModuleCore {
    @Single
    fun provideStartDestination(): StartDestination = DestinationA

    @Single
    fun provideCoroutineScope() = CoroutineScope(SupervisorJob())
}