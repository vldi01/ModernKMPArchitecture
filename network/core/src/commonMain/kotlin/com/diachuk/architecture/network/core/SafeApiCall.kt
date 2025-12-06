package com.diachuk.architecture.network.core

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

suspend inline fun <T> safeApiCall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: RedirectResponseException) {
        Result.failure(NetworkException.ApiError(e.response.status.value, e.message))
    } catch (e: ClientRequestException) {
        Result.failure(NetworkException.ApiError(e.response.status.value, e.message))
    } catch (e: ServerResponseException) {
        Result.failure(NetworkException.ApiError(e.response.status.value, e.message))
    } catch (e: SerializationException) {
        Result.failure(NetworkException.SerializationError(e))
    } catch (e: JsonConvertException) {
        Result.failure(NetworkException.SerializationError(e))
    } catch (e: NoTransformationFoundException) {
        Result.failure(NetworkException.SerializationError(e))
    } catch (e: IOException) {
        Result.failure(NetworkException.NoInternet(e))
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.failure(NetworkException.Unknown(e))
    }
}
