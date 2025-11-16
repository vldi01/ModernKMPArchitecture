package com.diachuk.architecture.core

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
    fun provideCoroutineScope() = CoroutineScope(SupervisorJob())
}