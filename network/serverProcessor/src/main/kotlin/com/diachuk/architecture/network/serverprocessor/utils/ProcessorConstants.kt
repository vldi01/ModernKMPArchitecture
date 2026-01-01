package com.diachuk.architecture.network.serverprocessor.utils

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter

object ProcessorConstants {
    // Annotations
    const val GET = "de.jensklingenberg.ktorfit.http.GET"
    const val POST = "de.jensklingenberg.ktorfit.http.POST"
    const val PUT = "de.jensklingenberg.ktorfit.http.PUT"
    const val DELETE = "de.jensklingenberg.ktorfit.http.DELETE"
    const val HEAD = "de.jensklingenberg.ktorfit.http.HEAD"
    const val PATCH = "de.jensklingenberg.ktorfit.http.PATCH"
    const val OPTIONS = "de.jensklingenberg.ktorfit.http.OPTIONS"

    const val BODY = "de.jensklingenberg.ktorfit.http.Body"
    const val PATH = "de.jensklingenberg.ktorfit.http.Path"
    const val QUERY = "de.jensklingenberg.ktorfit.http.Query"
    const val QUERY_MAP = "de.jensklingenberg.ktorfit.http.QueryMap"
    const val QUERY_NAME = "de.jensklingenberg.ktorfit.http.QueryName"
    const val MULTIPART = "de.jensklingenberg.ktorfit.http.Multipart"
    const val PART = "de.jensklingenberg.ktorfit.http.Part"

    // Custom Auth
    const val AUTH_JWT = "com.diachuk.architecture.network.core.AuthJwt"
    const val JWT_TYPE = "com.diachuk.architecture.network.core.JwtType"
    const val DEFAULT_JWT_TYPE = "com.diachuk.architecture.network.core.DefaultJwtType"
    const val NO_AUTH = "com.diachuk.architecture.network.core.NoAuth"
}

fun KSAnnotated.hasAnnotation(qualifiedName: String): Boolean {
    return annotations.any {
        it.annotationType.resolve().declaration.qualifiedName?.asString() == qualifiedName
    }
}

fun KSAnnotated.findAnnotation(qualifiedName: String): KSAnnotation? {
    return annotations.find {
        it.annotationType.resolve().declaration.qualifiedName?.asString() == qualifiedName
    }
}