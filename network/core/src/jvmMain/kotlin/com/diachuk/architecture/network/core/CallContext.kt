package com.diachuk.architecture.network.core

import io.ktor.server.routing.RoutingCall
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class CallContext<TOKEN : Any>(
    val call: RoutingCall,
    val jwtToken: TOKEN
) : AbstractCoroutineContextElement(CallContext) {
    companion object Key : CoroutineContext.Key<CallContext<*>>
}

suspend inline fun <reified T : Any> getContext(): CallContext<T> {
    val element = currentCoroutineContext()[CallContext.Key]
        ?: throw IllegalStateException("No CallContext found in current coroutine")

    if (element.jwtToken !is T) {
        throw ClassCastException(
            "CallContext contained token of type [${element.jwtToken::class.simpleName}], " +
                    "but requested [${T::class.simpleName}]"
        )
    }

    @Suppress("UNCHECKED_CAST")
    return element as CallContext<T>
}