package com.diachuk.architecture.network.core

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpClientPlugin

interface NetworkPluginProvider {
    fun HttpClientConfig<*>.provide(): HttpClientPlugin<*, *>
}
