package com.diachuk.architecture

import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.architecture.network.api.auth.AuthResponse
import com.diachuk.architecture.network.api.auth.LoginRequest
import com.diachuk.architecture.network.api.auth.RegisterRequest
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class AuthApiImpl : AuthApi {
    override suspend fun login(request: LoginRequest): AuthResponse {
        // In a real app, verify credentials here
        if (request.email == "test@example.com" && request.password == "password") {
            return AuthResponse(token = "valid-token-${UUID.randomUUID()}")
        }
        // For simplicity in this template, we just return a token
        return AuthResponse(token = "mock-login-token-${UUID.randomUUID()}")
    }

    override suspend fun register(request: RegisterRequest): AuthResponse {
        // In a real app, create user here
        return AuthResponse(token = "mock-register-token-${UUID.randomUUID()}")
    }
}