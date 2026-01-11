package com.diachuk.client.database

import androidx.room.RoomDatabaseConstructor

expect object DatabaseFactory : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
