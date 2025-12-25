package com.diachuk.architecture.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.architecture.network.api.auth.AuthResponse
import com.diachuk.architecture.network.api.auth.LoginRequest
import com.diachuk.architecture.network.api.auth.RegisterRequest
import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.architecture.user.UserDao
import com.diachuk.architecture.user.UserEntity
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull
import org.koin.core.annotation.Single
import java.util.Date

@Single
class AuthApiImpl(
    private val userDao: UserDao
) : AuthApi {
    // In a real app, store this securely and load from config
    private val secret = "secret"
    private val issuer = "http://0.0.0.0:8080/"
    private val audience = "http://0.0.0.0:8080/hello"

    override suspend fun login(request: LoginRequest): AuthResponse {
        val user = userDao.getUserByEmail(request.email)

        if (user != null && user.passwordHash == request.password) {
            val token = generateToken(user)
            return AuthResponse.Authorized(token = token)
        }

        return AuthResponse.InvalidCredentials
    }

    override suspend fun register(request: RegisterRequest): AuthResponse {
        val existing = userDao.getUserByEmail(request.email)
        if (existing != null) {
            return AuthResponse.UserAlreadyExists
        }

        val newUser = UserEntity(
            email = request.email,
            passwordHash = request.password,
            name = request.name
        )

        val id = userDao.insertUser(newUser)
        val savedUser = newUser.copy(id = id)

        val token = generateToken(savedUser)
        return AuthResponse.Authorized(token = token)
    }

    private fun generateToken(user: UserEntity): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .claimsAsJson(JwtEntity.UserToken(user.id.toString()))
            .withExpiresAt(Date(System.currentTimeMillis() + 60000 * 60 * 24))
            .sign(Algorithm.HMAC256(secret))
    }

    private inline fun <reified T> JWTCreator.Builder.claimsAsJson(data: T): JWTCreator.Builder {
        val json = Json.encodeToJsonElement(data)
        if (json is JsonObject) {
            for ((key, value) in json) {
                when (value) {
                    is JsonPrimitive -> {
                        if (value.isString) withClaim(key, value.content)
                        else if (value.booleanOrNull != null) withClaim(key, value.boolean)
                        else if (value.longOrNull != null) withClaim(key, value.long)
                        else if (value.doubleOrNull != null) withClaim(key, value.double)
                    }

                    is JsonArray -> {
                        val list = value.mapNotNull { (it as? JsonPrimitive)?.content }
                        withClaim(key, list)
                    }

                    else -> {}
                }
            }
        }
        return this
    }
}