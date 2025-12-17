package com.diachuk.architecture.network.core

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

@Single
class ClientBuilder : KoinComponent {
    private fun buildMainHttpClient(): HttpClient {
        val pluginProviders = getKoin().getAll<NetworkPluginProvider>()
        return HttpClient {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }

            pluginProviders
                .map {
                    with(it) { provide() }
                }
                .forEach {
                    install(it)
                }

            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }

    fun buildKtorfit(httpClient: HttpClient = buildMainHttpClient()): Ktorfit {
        return Ktorfit.Builder()
            .httpClient(httpClient)
            .baseUrl("http://192.168.0.17:8080/")
            .build()
    }
}
