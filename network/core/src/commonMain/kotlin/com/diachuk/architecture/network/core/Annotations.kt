package com.diachuk.architecture.network.core

@Target(AnnotationTarget.FUNCTION)
annotation class GET(val path: String)

@Target(AnnotationTarget.FUNCTION)
annotation class POST(val path: String)

@Target(AnnotationTarget.FUNCTION)
annotation class DELETE(val path: String)

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Path

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Body

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Query