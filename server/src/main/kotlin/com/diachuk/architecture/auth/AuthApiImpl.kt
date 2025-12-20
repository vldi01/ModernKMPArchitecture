package com.diachuk.architecture.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.diachuk.architecture.user.UserDao
import com.diachuk.architecture.user.UserEntity
import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.architecture.network.api.auth.AuthResponse
import com.diachuk.architecture.network.api.auth.LoginRequest
import com.diachuk.architecture.network.api.auth.RegisterRequest
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
            return AuthResponse(token = token)
        }

        throw Exception("Invalid credentials")
    }

    override suspend fun register(request: RegisterRequest): AuthResponse {
        val existing = userDao.getUserByEmail(request.email)
        if (existing != null) {
            throw Exception("User already exists")
        }

        val newUser = UserEntity(
            email = request.email,
            passwordHash = request.password,
            name = request.name
        )

        val id = userDao.insertUser(newUser)
        val savedUser = newUser.copy(id = id)

        val token = generateToken(savedUser)
        return AuthResponse(token = token)
    }

    private fun generateToken(user: UserEntity): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("email", user.email)
            .withClaim("id", user.id)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000 * 60 * 24))
            .sign(Algorithm.HMAC256(secret))
    }
}