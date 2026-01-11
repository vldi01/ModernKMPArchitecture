package com.diachuk.modernarchitecture.features.auth.api

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TokenEntity(
    @PrimaryKey val typeName: String,
    val token: String
)
