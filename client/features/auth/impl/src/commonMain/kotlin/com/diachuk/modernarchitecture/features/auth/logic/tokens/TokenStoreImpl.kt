package com.diachuk.modernarchitecture.features.auth.logic.tokens

import com.diachuk.modernarchitecture.features.auth.api.TokenDao
import com.diachuk.modernarchitecture.features.auth.api.TokenEntity
import com.diachuk.modernarchitecture.features.auth.api.TokenStore
import org.koin.core.annotation.Single
import kotlin.reflect.KClass

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