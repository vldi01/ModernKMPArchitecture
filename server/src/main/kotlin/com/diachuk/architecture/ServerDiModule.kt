package com.diachuk.architecture

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.diachuk.architecture.database.ServerDatabase
import com.diachuk.architecture.user.UserDao
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.io.File

@Module
@ComponentScan
object ServerDiModule {

    @Single
    fun provideDatabase(): ServerDatabase {
        val dbFile = File("server_database.db")
        return Room.databaseBuilder<ServerDatabase>(
            name = dbFile.absolutePath,
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @Single
    fun provideUserDao(db: ServerDatabase): UserDao = db.userDao()
}