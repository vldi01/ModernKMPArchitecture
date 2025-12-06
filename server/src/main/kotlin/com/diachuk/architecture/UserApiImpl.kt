package com.diachuk.architecture

import com.diachuk.architecture.network.api.user.CreateUserRequest
import com.diachuk.architecture.network.api.user.SearchResponse
import com.diachuk.architecture.network.api.user.User
import com.diachuk.architecture.network.api.user.UserApi
import org.koin.core.annotation.Single

@Single
class UserApiImpl : UserApi {
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
}