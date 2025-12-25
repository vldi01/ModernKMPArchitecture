package com.diachuk.modernarchitecture.features.auth.logic.tokens

import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.architecture.network.core.AuthJwt
import com.diachuk.architecture.network.core.NetworkPluginProvider
import com.diachuk.architecture.network.core.NoAuth
import com.diachuk.modernarchitecture.features.auth.api.TokenStore
import de.jensklingenberg.ktorfit.annotations
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import org.koin.core.annotation.Single

@Single(binds = [NetworkPluginProvider::class])
class AuthPluginProvider(private val tokenStore: TokenStore) : NetworkPluginProvider {

    override fun HttpClientConfig<*>.provide(): HttpClientPlugin<*, *> =
        createClientPlugin("AuthPlugin") {
            onRequest { request, _ ->
                val noAuthAnnotation = request.annotations
                    .filterIsInstance<NoAuth>()
                    .firstOrNull()

                if (noAuthAnnotation != null) {
                    return@onRequest
                }

                val authAnnotation = request.annotations
                    .filterIsInstance<AuthJwt>()
                    .firstOrNull()
                    ?: AuthJwt(JwtEntity.UserToken::class)

                val token = tokenStore.getToken(authAnnotation.klass)

                if (token != null) {
                    request.headers.append("Authorization", "Bearer $token")
                }
            }
        }
}
