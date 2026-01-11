package com.diachuk.architecture.network.core

sealed class NetworkException(message: String? = null, cause: Throwable? = null) :
    RuntimeException(message, cause) {
    data class ApiError(val code: Int, val description: String?) : NetworkException(description)
    data class NoInternet(val throwable: Throwable?) :
        NetworkException("Couldn't connect to the server", throwable)

    data class SerializationError(val throwable: Throwable?) :
        NetworkException("Data parsing error", throwable)

    data class Unknown(val throwable: Throwable?) : NetworkException("Unknown error", throwable)
}
