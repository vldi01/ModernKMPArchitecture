package com.diachuk.architecture.network.api.user

import com.diachuk.architecture.network.core.Body
import com.diachuk.architecture.network.core.DELETE
import com.diachuk.architecture.network.core.GET
import com.diachuk.architecture.network.core.POST
import com.diachuk.architecture.network.core.Path
import com.diachuk.architecture.network.core.Query

interface UserApi {
    @GET("/users/{id}")
    suspend fun getUser(@Path id: Long): User

    @POST("/users")
    suspend fun createUser(@Body request: CreateUserRequest): User

    @GET("/users/search")
    suspend fun searchUsers(
        @Query q: String,
        @Query limit: Int
    ): SearchResponse

    @DELETE("/users/{id}")
    suspend fun deleteUser(@Path id: Long): String
}