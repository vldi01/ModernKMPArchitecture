package com.diachuk.architecture.network.core

/**
 * Marks a data class as a JWT payload model.
 * The KSP processor will scan for all classes with this annotation to generate
 * the `configureJwt` extension function for Ktor's `AuthenticationConfig`.
 * This allows for strongly-typed validation of different token types.
 *
 * Example:
 * ```
 * @JwtType
 * @Serializable
 * data class UserToken(val userId: Long)
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JwtType()
