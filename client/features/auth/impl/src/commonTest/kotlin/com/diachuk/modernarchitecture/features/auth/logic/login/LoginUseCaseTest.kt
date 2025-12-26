package com.diachuk.modernarchitecture.features.auth.logic.login

import com.diachuk.architecture.network.api.auth.AuthApi
import com.diachuk.architecture.network.api.auth.AuthResponse
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

class LoginUseCaseTest {

    private val authApi = mock<AuthApi>()
    private val tokenStore = mock<TokenStore>()
    private val loginUseCase = LoginUseCase(authApi, tokenStore)

    @Test
    fun `execute returns Success when login is successful`() = runTest {
        everySuspend { authApi.login(any()) } returns AuthResponse.Authorized("valid_token")
        everySuspend { tokenStore.saveToken(any(), any()) } returns Unit

        val result = loginUseCase.execute("test@example.com", "password")

        assertTrue(result is LoginResult.Success)
        verifySuspend { tokenStore.saveToken(JwtEntity.UserToken::class, "valid_token") }
    }

    @Test
    fun `execute returns Error when login fails`() = runTest {
        everySuspend { authApi.login(any()) } returns AuthResponse.InvalidCredentials

        val result = loginUseCase.execute("test@example.com", "wrong_password")

        assertTrue(result is LoginResult.Error)
        assertEquals("Invalid credentials", (result as LoginResult.Error).message)
    }
}
