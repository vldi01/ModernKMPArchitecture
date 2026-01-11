package com.diachuk.client.database

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.diachuk.modernarchitecture.features.user.api.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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
        return AppDatabaseBuilderProvider
            .getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @Single
    fun provideTokenDao(appDatabase: AppDatabase) = appDatabase.tokenDao()

    @Single
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}