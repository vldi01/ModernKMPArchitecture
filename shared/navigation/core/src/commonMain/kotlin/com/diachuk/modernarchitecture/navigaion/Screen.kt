package com.diachuk.modernarchitecture.navigaion

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
annotation class Screen(
    val destination: KClass<out Destination>
)
