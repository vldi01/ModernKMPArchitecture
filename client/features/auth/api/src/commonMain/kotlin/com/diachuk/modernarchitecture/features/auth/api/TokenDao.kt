package com.diachuk.modernarchitecture.features.auth.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(token: TokenEntity)

    @Query("SELECT token FROM TokenEntity WHERE typeName = :typeName")
    suspend fun getToken(typeName: String): String?

    @Query("DELETE FROM TokenEntity WHERE typeName = :typeName")
    suspend fun delete(typeName: String)
}
