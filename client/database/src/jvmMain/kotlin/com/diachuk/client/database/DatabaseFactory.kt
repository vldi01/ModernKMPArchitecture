package com.diachuk.client.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = File(System.getProperty("user.home"), "app.db")
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath
        )
    }
}
