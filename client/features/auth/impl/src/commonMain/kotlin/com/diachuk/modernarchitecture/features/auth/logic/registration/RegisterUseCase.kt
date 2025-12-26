package com.diachuk.modernarchitecture.features.auth.logic.registration

import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.architecture.network.api.auth.AuthResponse
import com.diachuk.architecture.network.api.auth.RegisterRequest
import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.architecture.network.core.safeApiCall
import com.diachuk.modernarchitecture.features.auth.api.TokenStore
import org.koin.core.annotation.Factory

@Factory
class RegisterUseCase(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore
) {
    suspend fun execute(request: RegisterRequest): RegisterResult {
        val apiResult = safeApiCall { authApi.register(request) }
            .onSuccess { response ->
                return when (response) {
                    is AuthResponse.Authorized -> {
                        tokenStore.saveToken(JwtEntity.UserToken::class, response.token)
                        RegisterResult.Success
                    }
                    AuthResponse.UserAlreadyExists -> RegisterResult.Error("User already exists")
                    AuthResponse.InvalidCredentials -> RegisterResult.Error("Invalid credentials")
                }
            }
        return RegisterResult.Error(apiResult.exceptionOrNull()?.message ?: "Registration failed")
    }
}

sealed interface RegisterResult {
    data object Success : RegisterResult
    data class Error(val message: String) : RegisterResult
}
