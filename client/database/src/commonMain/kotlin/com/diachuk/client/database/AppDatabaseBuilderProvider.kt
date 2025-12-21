package com.diachuk.client.database

import androidx.room.RoomDatabase
import org.koin.core.annotation.Factory

expect object AppDatabaseBuilderProvider {
    fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
}