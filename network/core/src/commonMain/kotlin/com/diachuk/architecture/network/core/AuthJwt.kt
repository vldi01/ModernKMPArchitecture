package com.diachuk.architecture.network.core

import kotlin.reflect.KClass

/**
 * Annotates a Ktorfit interface method to specify that it requires JWT authentication.
 * The generated server-side route for this method will be wrapped in a Ktor `authenticate` block.
 *
 * @property klass The specific JWT payload class (e.g., `UserToken::class`) to be used for this endpoint.
 *                 This class must be annotated with [JwtType]. The processor uses this to configure
 *                 the correct JWT authenticator.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthJwt(val klass: KClass<*>)
