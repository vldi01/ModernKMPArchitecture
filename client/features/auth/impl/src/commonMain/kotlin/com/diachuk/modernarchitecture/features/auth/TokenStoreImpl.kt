package com.diachuk.modernarchitecture.features.auth

import com.diachuk.client.database.AppDatabase
import kotlin.reflect.KClass
import org.koin.core.annotation.Single

@Single(binds = [TokenStore::class])
class TokenStoreImpl(private val tokenDao: TokenDao) : TokenStore {
    override suspend fun saveToken(type: KClass<*>, token: String) {
        val typeName = type.qualifiedName ?: type.simpleName ?: "unknown"
        tokenDao.insert(TokenEntity(typeName, token))
    }

    override suspend fun getToken(type: KClass<*>): String? {
        val typeName = type.qualifiedName ?: type.simpleName ?: "unknown"
        return tokenDao.getToken(typeName)
    }

    override suspend fun clearToken(type: KClass<*>) {
        val typeName = type.qualifiedName ?: type.simpleName ?: "unknown"
        tokenDao.delete(typeName)
    }
}
