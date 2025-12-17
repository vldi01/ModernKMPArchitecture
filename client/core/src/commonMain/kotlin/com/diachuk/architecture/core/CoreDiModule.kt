package com.diachuk.architecture.core

import com.diachuk.architecture.network.api.user.NetworkApiDiModule
import com.diachuk.architecture.network.core.NetworkCoreDiModule
import com.diachuk.client.database.DatabaseModule
import com.diachuk.modernarchitecture.features.a.DIModuleA
import com.diachuk.modernarchitecture.features.auth.AuthDiModule
import com.diachuk.modernarchitecture.features.b.DIModuleB
import com.diachuk.modernarchitecture.navigaion.NavigationDi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module(
    includes = [
        // TODO: remove when koin fix multi-module scanning
        DIModuleA::class,
        DIModuleB::class,
        AuthDiModule::class,
        NavigationDi::class,
        NetworkApiDiModule::class,
        NetworkCoreDiModule::class,
        DatabaseModule::class
    ]
)
@Configuration
@ComponentScan
object CoreDiModule {
    @Single
    fun provideCoroutineScope() = CoroutineScope(SupervisorJob())
}