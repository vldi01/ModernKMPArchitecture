package com.diachuk.architecture

//import com.diachuk.architecture.network.api.user.bindUserApi
import com.diachuk.architecture.network.api.user.CreateUserRequest
import com.diachuk.architecture.network.api.user.SearchResponse
import com.diachuk.architecture.network.api.user.User
import com.diachuk.architecture.network.api.user.UserApi
import com.diachuk.architecture.network.api.user.bindUserApi
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    routing {
        get("/") {
            call.respondText("Ktor: Hello World")
        }

        bindUserApi(object : UserApi {
            override suspend fun getUser(id: Long): User {
                return User(id, "User $id", "user$id@example.com")
            }

            override suspend fun createUser(request: CreateUserRequest): User {
                return User(123, request.name, request.email)
            }

            override suspend fun searchUsers(q: String, limit: Int): SearchResponse {
                return SearchResponse(
                    listOf(User(1, "Alice", "alice@example.com")),
                    1
                )
            }

            override suspend fun deleteUser(id: Long): String {
                return "User $id deleted"
            }
        })
    }
}
