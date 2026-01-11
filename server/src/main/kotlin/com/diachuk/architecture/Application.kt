package com.diachuk.architecture

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.diachuk.architecture.auth.AuthApiImpl
import com.diachuk.architecture.network.api.auth.bindAuthApi
import com.diachuk.architecture.network.api.user.bindUserApi
import com.diachuk.architecture.network.server.configureJwt
import com.diachuk.architecture.user.UserApiImpl
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.koin.ksp.generated.module
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(ServerDiModule.module)
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(Authentication) {
        configureJwt(
            JWT
                .require(Algorithm.HMAC256("secret"))
                .withAudience("http://0.0.0.0:8080/hello")
                .withIssuer("http://0.0.0.0:8080/")
                .build()
        )
    }

    routing {
        get("/") {
            call.respondText("Ktor: Hello World")
        }

        bindUserApi(get<UserApiImpl>())
        bindAuthApi(get<AuthApiImpl>())
    }
}