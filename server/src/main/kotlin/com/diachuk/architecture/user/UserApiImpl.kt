package com.diachuk.architecture.user

import com.diachuk.architecture.network.api.user.JwtEntity
import com.diachuk.architecture.network.api.user.User
import com.diachuk.architecture.network.api.user.UserApi
import com.diachuk.architecture.network.core.getContext
import org.koin.core.annotation.Single

@Single
class UserApiImpl(private val userDao: UserDao) : UserApi {
    override suspend fun getUser(id: Long): User {
        val entity = userDao.getUserById(id) ?: throw Exception("User not found")
        return User(entity.id, entity.name, entity.email)
    }

    override suspend fun searchUsers(q: String, limit: Int?): List<User> {
        val entities = if (q.isBlank()) {
            userDao.getAllUsers()
        } else {
            userDao.searchUsers(q)
        }

        val users = entities.map { User(it.id, it.name, it.email) }
        val result = if (limit != null) users.take(limit) else users

        return result
    }
}
