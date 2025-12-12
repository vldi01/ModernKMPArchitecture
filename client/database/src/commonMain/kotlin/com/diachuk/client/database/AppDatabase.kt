package com.diachuk.client.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diachuk.client.features.user.api.UserDao
import com.diachuk.modernarchitecture.features.user.api.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
