package com.diachuk.modernarchitecture.features.auth.logic.tokens

import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.architecture.network.core.AuthJwt
import com.diachuk.architecture.network.core.NoAuth
import com.diachuk.modernarchitecture.features.auth.api.TokenStore
import org.koin.core.annotation.Single

@Single
class AuthTokenResolver(private val tokenStore: TokenStore) {

    suspend fun resolveToken(annotations: List<Any>): String? {
        val noAuthAnnotation = annotations
            .filterIsInstance<NoAuth>()
            .firstOrNull()

        if (noAuthAnnotation != null) {
            return null
        }

        val authAnnotation = annotations
            .filterIsInstance<AuthJwt>()
            .firstOrNull()
            ?: AuthJwt(JwtEntity.UserToken::class)

        return tokenStore.getToken(authAnnotation.klass)
    }
}
