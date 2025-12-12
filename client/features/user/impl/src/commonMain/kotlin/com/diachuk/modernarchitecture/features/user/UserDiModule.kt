package com.diachuk.modernarchitecture.features.user

import com.diachuk.client.database.AppDatabase
import com.diachuk.modernarchitecture.features.user.api.UserDao
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan
@Configuration
object UserDiModule {
    @Single
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}