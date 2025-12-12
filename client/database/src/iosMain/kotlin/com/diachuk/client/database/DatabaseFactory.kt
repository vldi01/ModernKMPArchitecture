package com.diachuk.client.database

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = NSHomeDirectory() + "/app.db"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFilePath
        )
    }
}
