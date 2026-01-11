package com.diachuk.modernarchitecture.features.auth.logic.tokens

import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.architecture.network.core.AuthJwt
import com.diachuk.architecture.network.core.NoAuth
import com.diachuk.modernarchitecture.features.auth.api.TokenStore
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AuthTokenResolverTest {

    private val tokenStore = mock<TokenStore>()
    private val authTokenResolver = AuthTokenResolver(tokenStore)

    @Test
    fun `default behavior returns UserToken when no annotations present`() = runTest {
        everySuspend { tokenStore.getToken(JwtEntity.UserToken::class) } returns "user_token"

        val result = authTokenResolver.resolveToken(emptyList())

        assertEquals("user_token", result)
    }

    @Test
    fun `NoAuth annotation returns null`() = runTest {
        val result = authTokenResolver.resolveToken(listOf(NoAuth()))

        assertNull(result)
    }

    @Test
    fun `AuthJwt annotation returns specific token`() = runTest {
        everySuspend { tokenStore.getToken(JwtEntity.UserToken::class) } returns "custom_token"

        val result = authTokenResolver.resolveToken(listOf(AuthJwt(JwtEntity.UserToken::class)))

        assertEquals("custom_token", result)
    }

    @Test
    fun `missing token returns null`() = runTest {
        everySuspend { tokenStore.getToken(any()) } returns null

        val result = authTokenResolver.resolveToken(emptyList())

        assertNull(result)
    }
}
