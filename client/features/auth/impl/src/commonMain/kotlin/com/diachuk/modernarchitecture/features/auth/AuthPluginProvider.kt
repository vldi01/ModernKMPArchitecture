package com.diachuk.modernarchitecture.features.auth

import com.diachuk.architecture.network.core.AuthJwt
import com.diachuk.architecture.network.core.NetworkPluginProvider
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
                val authAnnotation = request.annotations.filterIsInstance<AuthJwt>().firstOrNull()
                    ?: return@onRequest

                val token = tokenStore.getToken(authAnnotation.klass)

                if (token != null) {
                    request.headers.append("Authorization", "Bearer $token")
                }
            }
        }
}
