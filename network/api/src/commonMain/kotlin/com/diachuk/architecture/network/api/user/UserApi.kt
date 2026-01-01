package com.diachuk.architecture.network.api.user

import com.diachuk.architecture.network.core.AuthJwt
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface UserApi {
    @GET("users/{id}")
    suspend fun getUser(@Path id: Long): User

    @GET("users/search")
    suspend fun searchUsers(
        @Query q: String,
        @Query limit: Int?
    ): List<User>
}