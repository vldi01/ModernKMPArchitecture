package com.diachuk.architecture.network.api.user

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Part
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.content.PartData

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

    @Multipart
    @POST("upload")
    suspend fun uploadFile(@Part("description") description: String, @Part("") file: List<PartData>): String

}