package com.diachuk.client.database

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

actual object AppDatabaseBuilderProvider : KoinComponent {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val appContext = get<Application>().applicationContext
        val dbFile = appContext.getDatabasePath("app_database.db")
        return Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}