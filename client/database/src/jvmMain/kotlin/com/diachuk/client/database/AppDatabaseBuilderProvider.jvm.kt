package com.diachuk.client.database

import androidx.room.RoomDatabase
import org.koin.core.component.KoinComponent

actual object AppDatabaseBuilderProvider : KoinComponent {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        TODO("Not yet implemented")
    }
}