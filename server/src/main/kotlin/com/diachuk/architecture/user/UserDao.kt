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

    @Query("SELECT * FROM UserEntity")
    suspend fun getAllUsers(): List<UserEntity>
    
    @Query("SELECT * FROM UserEntity WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<UserEntity>
    
    @Query("SELECT * FROM UserEntity WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?
    
    @Query("DELETE FROM UserEntity WHERE id = :id")
    suspend fun deleteUserById(id: Long)
}
