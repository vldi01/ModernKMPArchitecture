package com.diachuk.architecture

import com.diachuk.architecture.network.api.user.CreateUserRequest
import com.diachuk.architecture.network.api.user.SearchResponse
import com.diachuk.architecture.network.api.user.User
import com.diachuk.architecture.network.api.user.UserApi
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveMultipart
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

    override suspend fun uploadFile(description: String, file: List<PartData>): String {
        var fileName = "unknown"
        var fileSize = 0

        file.forEach { part ->
            if (part is PartData.FileItem) {
                fileName = part.originalFileName ?: "unknown"
                val fileBytes = part.streamProvider().readBytes()
                fileSize = fileBytes.size
                // TODO: Save the file here
            }
            part.dispose()
        }

        return "Uploaded $fileName ($fileSize bytes) with description: $description"
    }
}