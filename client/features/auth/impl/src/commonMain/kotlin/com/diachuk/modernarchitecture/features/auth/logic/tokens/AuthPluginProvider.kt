package com.diachuk.modernarchitecture.features.auth.logic.tokens

import com.diachuk.architecture.network.core.NetworkPluginProvider
import de.jensklingenberg.ktorfit.annotations
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import org.koin.core.annotation.Single

@Single(binds = [NetworkPluginProvider::class])
class AuthPluginProvider(private val authTokenResolver: AuthTokenResolver) : NetworkPluginProvider {

    override fun HttpClientConfig<*>.provide(): HttpClientPlugin<*, *> =
        createClientPlugin("AuthPlugin") {
            onRequest { request, _ ->
                val token = authTokenResolver.resolveToken(request.annotations)

                if (token != null) {
                    request.headers.append("Authorization", "Bearer $token")
                }
            }
        }
}
