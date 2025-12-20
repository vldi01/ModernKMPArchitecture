package com.diachuk.architecture.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diachuk.architecture.user.UserDao
import com.diachuk.architecture.user.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class ServerDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}