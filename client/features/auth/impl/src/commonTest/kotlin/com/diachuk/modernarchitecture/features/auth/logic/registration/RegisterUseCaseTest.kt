package com.diachuk.modernarchitecture.features.auth.logic.registration

import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.architecture.network.api.auth.AuthResponse
import com.diachuk.architecture.network.api.auth.RegisterRequest
import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.modernarchitecture.features.auth.api.TokenStore
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterUseCaseTest {

    private val authApi = mock<AuthApi>()
    private val tokenStore = mock<TokenStore>()
    private val registerUseCase = RegisterUseCase(authApi, tokenStore)

    @Test
    fun `execute returns Success when registration is successful`() = runTest {
        everySuspend { authApi.register(any()) } returns AuthResponse.Authorized("new_token")
        everySuspend { tokenStore.saveToken(any(), any()) } returns Unit

        val request = RegisterRequest("Test User", "test@example.com", "password")
        val result = registerUseCase.execute(request)

        assertTrue(result is RegisterResult.Success)
        verifySuspend { tokenStore.saveToken(JwtEntity.UserToken::class, "new_token") }
    }

    @Test
    fun `execute returns Error when user already exists`() = runTest {
        everySuspend { authApi.register(any()) } returns AuthResponse.UserAlreadyExists

        val request = RegisterRequest("Existing User", "exist@example.com", "password")
        val result = registerUseCase.execute(request)

        assertTrue(result is RegisterResult.Error)
        assertEquals("User already exists", (result as RegisterResult.Error).message)
    }
}
