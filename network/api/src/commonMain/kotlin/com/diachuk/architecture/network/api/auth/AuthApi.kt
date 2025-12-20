package com.diachuk.architecture.network.api.auth

import com.diachuk.architecture.network.core.NoAuth
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface AuthApi {
    @POST("auth/login")
    @NoAuth
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    @NoAuth
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}