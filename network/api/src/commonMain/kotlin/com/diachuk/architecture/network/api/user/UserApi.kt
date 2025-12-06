package com.diachuk.architecture.network.api.user

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface UserApi {
    @GET("users/{id}")
    suspend fun getUser(@Path id: Long): User

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): User

    @GET("users/search")
    suspend fun searchUsers(
        @Query q: String,
        @Query limit: Int
    ): SearchResponse

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path id: Long): String
}