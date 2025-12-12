package com.diachuk.architecture.network.serverprocessor

import com.diachuk.architecture.network.core.AuthJwt
import com.diachuk.architecture.network.core.JwtType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.Part
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.QueryName
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route

class RouteGenerator(
    private val codeGenerator: CodeGenerator
) {

    fun generateServerRoute(interfaceDecl: KSClassDeclaration) {
        val packageName = interfaceDecl.packageName.asString()
        val interfaceName = interfaceDecl.simpleName.asString()
        val fileName = "${interfaceName}Routes"

        val fileSpec = FileSpec.builder(packageName, fileName)

        val routeClass = Route::class.asClassName()

        val funSpec = FunSpec.builder("bind$interfaceName")
            .receiver(routeClass)
            .addParameter("impl", interfaceDecl.asStarProjectedType().toTypeName())

        interfaceDecl.getAllFunctions().forEach { function ->
            if (function.isAbstract) {
                val httpMethod = getHttpMethod(function)
                if (httpMethod != null) {
                    addRouteBlock(funSpec, function, httpMethod, "impl")
                }
            }
        }

        fileSpec.addFunction(funSpec.build())
        fileSpec.build().writeTo(codeGenerator, Dependencies(true, interfaceDecl.containingFile!!))
    }

    private fun addRouteBlock(
        builder: FunSpec.Builder,
        function: KSFunctionDeclaration,
        methodInfo: HttpMethodInfo,
        implName: String
    ) {
        val authJwtName = AuthJwt::class.qualifiedName!!
        val jwtTypeName = JwtType::class.qualifiedName!!

        val authAnnotation = function.annotations.find {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == authJwtName
        }

        var authName: String? = null

        if (authAnnotation != null) {
            val arg = authAnnotation.arguments.firstOrNull { it.name?.asString() == "klass" }
                ?: authAnnotation.arguments.firstOrNull()

            val type = arg?.value as? KSType
            val decl = type?.declaration

            if (decl != null) {
                val isJwtType = decl.annotations.any {
                    it.annotationType.resolve().declaration.qualifiedName?.asString() == jwtTypeName
                }
                if (!isJwtType) {
                    throw IllegalArgumentException("Class ${decl.qualifiedName?.asString() ?: decl.simpleName.asString()} used in @AuthJwt must be annotated with @JwtType")
                }
                authName = decl.simpleName.asString()
            }
        }

        if (authName != null) {
            val authenticateFunc = MemberName("io.ktor.server.auth", "authenticate")
            builder.beginControlFlow("%M(%S) {", authenticateFunc, authName)
        }

        val routeFunc = MemberName("io.ktor.server.routing", methodInfo.type)

        builder.beginControlFlow("%M(%S) {", routeFunc, methodInfo.path)

        val isMultipart = function.annotations.any {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == Multipart::class.qualifiedName
        }

        val multipartParts = mutableListOf<KSValueParameter>()
        val args = mutableListOf<String>()

        function.parameters.forEach { param ->
            val paramName = param.name!!.asString()
            val type = param.type.resolve()
            val typeName = type.toTypeName()
            val qualifiedType = type.declaration.qualifiedName?.asString()

            val isPath =
                param.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == Path::class.qualifiedName }
            val isQuery =
                param.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == Query::class.qualifiedName }
            val isBody =
                param.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == Body::class.qualifiedName }
            val isQueryMap =
                param.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == QueryMap::class.qualifiedName }
            val isQueryName =
                param.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == QueryName::class.qualifiedName }
            val isPart =
                param.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == Part::class.qualifiedName }

            if (isMultipart && isPart) {
                multipartParts.add(param)
                args.add(paramName)
            } else if (isBody) {
                if (qualifiedType == "io.ktor.client.request.forms.MultiPartFormDataContent") {
                    val receiveMultipart = MemberName("io.ktor.server.request", "receiveMultipart")
                    val partDataClassName = ClassName("io.ktor.http.content", "PartData")

                    builder.addStatement(
                        "val %L_parts = mutableListOf<%T>()",
                        paramName,
                        partDataClassName
                    )
                    builder.addStatement(
                        "val %L_multipart = call.%M()",
                        paramName,
                        receiveMultipart
                    )
                    builder.beginControlFlow("while (true)")
                    builder.addStatement("val part = %L_multipart.readPart() ?: break", paramName)
                    builder.addStatement("%L_parts.add(part)", paramName)
                    builder.endControlFlow()
                    builder.addStatement("val %L = %T(%L_parts)", paramName, typeName, paramName)

                } else if (qualifiedType == "io.ktor.http.content.MultiPartData") {
                    val receiveMultipart = MemberName("io.ktor.server.request", "receiveMultipart")
                    builder.addStatement("val %L = call.%M()", paramName, receiveMultipart)
                } else {
                    val receiveFunc = MemberName("io.ktor.server.request", "receive")
                    builder.addStatement("val %L = call.%M<%T>()", paramName, receiveFunc, typeName)
                }
                args.add(paramName)
            } else if (isQueryMap) {
                builder.addStatement(
                    "val %L = call.request.queryParameters.entries().associate { it.key to (it.value.firstOrNull() ?: \"\") }",
                    paramName
                )
                args.add(paramName)
            } else if (isQueryName) {
                if (type.isMarkedNullable) {
                    builder.addStatement("val %L = null", paramName)
                } else {
                    builder.addStatement("val %L = \"\"", paramName)
                }
                args.add(paramName)
            } else if (isPath || isQuery) {
                val accessor = if (isQuery) "request.queryParameters" else "parameters"
                val paramKey = paramName

                val conversion = when (qualifiedType) {
                    "kotlin.Long" -> ".toLongOrNull()"
                    "kotlin.Int" -> ".toIntOrNull()"
                    "kotlin.Boolean" -> ".toBooleanStrictOrNull()"
                    "kotlin.Double" -> ".toDoubleOrNull()"
                    "kotlin.Float" -> ".toFloatOrNull()"
                    else -> ""
                }

                val nullable = type.isMarkedNullable

                if (nullable) {
                    builder.addStatement(
                        "val %L = call.%L[%S]?%L",
                        paramName,
                        accessor,
                        paramKey,
                        conversion
                    )
                } else {
                    val httpStatusCode = HttpStatusCode::class.asClassName()
                    val respond = MemberName("io.ktor.server.response", "respond")

                    if (qualifiedType == "kotlin.String") {
                        if (isQuery) {
                            builder.addStatement(
                                "val %L = call.%L[%S] ?: \"\"",
                                paramName,
                                accessor,
                                paramKey
                            )
                        } else {
                            builder.addStatement(
                                "val %L = call.%L[%S] ?: return@%L call.%M(%T.BadRequest, \"Missing %L\")",
                                paramName,
                                accessor,
                                paramKey,
                                methodInfo.type,
                                respond,
                                httpStatusCode,
                                paramKey
                            )
                        }
                    } else {
                        builder.addStatement(
                            "val %L = call.%L[%S]?%L ?: return@%L call.%M(%T.BadRequest, \"Invalid %L\")",
                            paramName,
                            accessor,
                            paramKey,
                            conversion,
                            methodInfo.type,
                            respond,
                            httpStatusCode,
                            paramKey
                        )
                    }
                }
                args.add(paramName)
            }
        }

        if (multipartParts.isNotEmpty()) {
            multipartParts.forEach { param ->
                val paramName = param.name!!.asString()
                val type = param.type.resolve()
                val typeName = type.toTypeName()
                val qualifiedType = type.declaration.qualifiedName?.asString()
                val partAnnotation =
                    param.annotations.find { it.annotationType.resolve().declaration.qualifiedName?.asString() == Part::class.qualifiedName }
                val partName =
                    partAnnotation?.arguments?.firstOrNull()?.value as? String ?: paramName

                if (qualifiedType == "kotlin.String") {
                    builder.addStatement("var %L: String? = null", paramName)
                } else if (qualifiedType == "kotlin.collections.List") {
                    val partDataClassName = ClassName("io.ktor.http.content", "PartData")
                    builder.addStatement(
                        "val %L = mutableListOf<%T>()",
                        paramName,
                        partDataClassName
                    )
                }
            }

            val receiveMultipart = MemberName("io.ktor.server.request", "receiveMultipart")
            builder.addStatement("val multipart = call.%M()", receiveMultipart)

            builder.beginControlFlow("while (true)")
            builder.addStatement("val part = multipart.readPart() ?: break")

            builder.beginControlFlow("when (part.name)")

            multipartParts.forEach { param ->
                val paramName = param.name!!.asString()
                val type = param.type.resolve()
                val qualifiedType = type.declaration.qualifiedName?.asString()
                val partAnnotation =
                    param.annotations.find { it.annotationType.resolve().declaration.qualifiedName?.asString() == Part::class.qualifiedName }
                val partName = partAnnotation?.arguments?.firstOrNull()?.value as? String ?: ""

                if (partName.isNotEmpty()) {
                    builder.beginControlFlow("%S ->", partName)
                    if (qualifiedType == "kotlin.String") {
                        builder.addStatement(
                            "if (part is io.ktor.http.content.PartData.FormItem) { %L = part.value }",
                            paramName
                        )
                    }
                    builder.endControlFlow()
                }
            }

            builder.endControlFlow() // End when

            // Collect catch-all parts (empty name in @Part)
            multipartParts.forEach { param ->
                val paramName = param.name!!.asString()
                val partAnnotation =
                    param.annotations.find { it.annotationType.resolve().declaration.qualifiedName?.asString() == Part::class.qualifiedName }
                val partName = partAnnotation?.arguments?.firstOrNull()?.value as? String ?: ""

                if (partName.isEmpty()) {
                    builder.addStatement("%L.add(part)", paramName)
                }
            }

            builder.endControlFlow() // End while

            // Finalize String params
            multipartParts.forEach { param ->
                val paramName = param.name!!.asString()
                val type = param.type.resolve()
                val qualifiedType = type.declaration.qualifiedName?.asString()
                if (qualifiedType == "kotlin.String") {
                    val isNullable = type.isMarkedNullable
                    if (!isNullable) {
                        builder.addStatement("val %L_final = %L ?: \"\"", paramName, paramName)
                        // If strict, we should return BadRequest if null
                    } else {
                        builder.addStatement("val %L_final = %L", paramName, paramName)
                    }
                }
            }
        }

        // Fix args to use _final for Strings in multipart
        val finalArgs = args.map { arg ->
            val isMultipartString =
                multipartParts.any { it.name!!.asString() == arg && it.type.resolve().declaration.qualifiedName?.asString() == "kotlin.String" }
            if (isMultipartString) "${arg}_final" else arg
        }

        val respondFunc = MemberName("io.ktor.server.response", "respond")
        builder.addStatement(
            "call.%M(%L.%L(%L))",
            respondFunc,
            implName,
            function.simpleName.asString(),
            finalArgs.joinToString(", ")
        )

        builder.endControlFlow()

        if (authName != null) {
            builder.endControlFlow()
        }
    }
}
