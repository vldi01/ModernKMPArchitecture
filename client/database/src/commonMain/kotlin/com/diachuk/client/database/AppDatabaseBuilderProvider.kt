package com.diachuk.client.database

import androidx.room.RoomDatabase

expect object AppDatabaseBuilderProvider {
    fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
}