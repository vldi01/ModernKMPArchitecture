package com.diachuk.modernarchitecture.features.auth.api

import kotlin.reflect.KClass

interface TokenStore {
    suspend fun saveToken(type: KClass<*>, token: String)
    suspend fun getToken(type: KClass<*>): String?
    suspend fun clearToken(type: KClass<*>)
}
