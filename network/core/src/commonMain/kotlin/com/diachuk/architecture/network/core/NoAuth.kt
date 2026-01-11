package com.diachuk.architecture.network.core

/**
 * Annotates a Ktorfit interface method to explicitly exclude it from JWT authentication.
 * Use this for public endpoints that do not require a token, even if a [DefaultJwtType]
 * is configured for the API.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoAuth()
