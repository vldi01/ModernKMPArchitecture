package com.diachuk.architecture.network.core

/**
 * Marks a data class as the default JWT payload type for a given API interface.
 * When an interface method is not annotated with [AuthJwt] or [NoAuth], the processor
 * will assume it requires authentication using this default token type.
 *
 * An API interface can only have one default JWT type associated with it through its methods' authentications.
 *
 * This annotation must be used on a class that is also annotated with [JwtType].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultJwtType()
