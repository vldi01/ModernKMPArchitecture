package com.diachuk.architecture.network.core

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthJwt(val klass: KClass<*>)
