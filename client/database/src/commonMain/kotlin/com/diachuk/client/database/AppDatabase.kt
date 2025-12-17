package com.diachuk.client.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.diachuk.modernarchitecture.features.auth.TokenDao
import com.diachuk.modernarchitecture.features.auth.TokenEntity
import com.diachuk.modernarchitecture.features.user.api.UserDao
import com.diachuk.modernarchitecture.features.user.api.UserEntity

@Database(entities = [UserEntity::class, TokenEntity::class], version = 1)
@ConstructedBy(DatabaseFactory::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tokenDao(): TokenDao
}
