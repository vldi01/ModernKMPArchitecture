package com.diachuk.modernarchitecture.features.user.api

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey val id: Long,
    val name: String
)