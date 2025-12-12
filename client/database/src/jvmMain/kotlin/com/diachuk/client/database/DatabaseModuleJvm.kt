package com.diachuk.client.database

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
object DatabaseModuleJvm {
    @Single
    fun provideDatabase(): AppDatabase {
        return createDatabase(DatabaseFactory().create())
    }
}
