package com.diachuk.client.database

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan
@Configuration
object DatabaseModule {
    @Single
    fun provideDatabase(): AppDatabase {
        return DatabaseFactory.initialize()
    }
}