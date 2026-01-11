package com.diachuk.modernarchitecture.features.user.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM UserEntity")
    fun getAll(): Flow<List<UserEntity>>
}