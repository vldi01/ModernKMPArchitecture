package com.diachuk.architecture.network.serverprocessor

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HEAD
import de.jensklingenberg.ktorfit.http.OPTIONS
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT

data class HttpMethodInfo(val type: String, val path: String)

internal fun getHttpMethod(function: KSFunctionDeclaration): HttpMethodInfo? {
    val annotations = mapOf(
        GET::class.qualifiedName!! to "get",
        POST::class.qualifiedName!! to "post",
        PUT::class.qualifiedName!! to "put",
        DELETE::class.qualifiedName!! to "delete",
        HEAD::class.qualifiedName!! to "head",
        OPTIONS::class.qualifiedName!! to "options",
        PATCH::class.qualifiedName!! to "patch"
    )

    for ((annotationName, method) in annotations) {
        val annotation = function.annotations.find {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationName
        }
        if (annotation != null) {
            val path =
                annotation.arguments.firstOrNull { it.name?.asString() == "value" || it.name?.asString() == "path" || it.name == null }?.value as? String
                    ?: ""
            return HttpMethodInfo(method, path)
        }
    }
    return null
}
