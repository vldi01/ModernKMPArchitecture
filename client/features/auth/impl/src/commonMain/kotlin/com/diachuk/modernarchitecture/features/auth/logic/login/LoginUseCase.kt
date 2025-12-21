package com.diachuk.modernarchitecture.features.auth.logic.login

import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.architecture.network.api.auth.AuthResponse
import com.diachuk.architecture.network.api.auth.LoginRequest
import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.architecture.network.core.safeApiCall
import com.diachuk.modernarchitecture.features.auth.api.TokenStore
import org.koin.core.annotation.Factory

@Factory
class LoginUseCase(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
) {
    suspend fun execute(email: String, password: String): LoginResult {
        val apiResult = safeApiCall { authApi.login(LoginRequest(email, password)) }
            .onSuccess { response ->
                return when (response) {
                    is AuthResponse.Authorized -> {
                        tokenStore.saveToken(JwtEntity.UserToken::class, response.token)
                        LoginResult.Success
                    }

                    AuthResponse.InvalidCredentials -> LoginResult.Error("Invalid credentials")
                    AuthResponse.UserAlreadyExists -> LoginResult.Error("User already exists")
                }
            }
        return LoginResult.Error(apiResult.exceptionOrNull()?.message ?: "Login failed")
    }
}