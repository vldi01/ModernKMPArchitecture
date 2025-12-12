package com.diachuk.client.database

import android.content.Context
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan
@Configuration
object DatabaseModuleAndroid {
    @Single(binds = [AppDatabase::class])
    fun provideDatabase(context: Context): AppDatabase {
        return createDatabase(DatabaseFactory(context).create())
    }
}