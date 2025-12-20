package com.diachuk.architecture.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert
    suspend fun insertUser(user: UserEntity): Long
}