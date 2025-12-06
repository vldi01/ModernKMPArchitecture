package com.diachuk.architecture.network.serverprocessor

import com.diachuk.architecture.network.core.Body
import com.diachuk.architecture.network.core.DELETE
import com.diachuk.architecture.network.core.GET
import com.diachuk.architecture.network.core.POST
import com.diachuk.architecture.network.core.Path
import com.diachuk.architecture.network.core.Query
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route

class ServerProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = (
                resolver.getSymbolsWithAnnotation(GET::class.qualifiedName!!) +
                        resolver.getSymbolsWithAnnotation(POST::class.qualifiedName!!) +
                        resolver.getSymbolsWithAnnotation(DELETE::class.qualifiedName!!)
                ).distinct()

        val validSymbols = symbols.filter { it.validate() }.toList()

        val interfaces = validSymbols.filterIsInstance<KSFunctionDeclaration>()
            .mapNotNull { it.parentDeclaration as? KSClassDeclaration }
            .distinct()

        interfaces.forEach { interfaceDecl ->
            generateServerRoute(interfaceDecl)
        }

        return symbols.filterNot { it.validate() }.toList()
    }

    private fun generateServerRoute(interfaceDecl: KSClassDeclaration) {
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

    data class HttpMethodInfo(val type: String, val path: String)

    private fun getHttpMethod(function: KSFunctionDeclaration): HttpMethodInfo? {
        val annotations = mapOf(
            GET::class.qualifiedName!! to "get",
            POST::class.qualifiedName!! to "post",
            DELETE::class.qualifiedName!! to "delete"
        )

        for ((annotationName, method) in annotations) {
            val annotation = function.annotations.find {
                it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationName
            }
            if (annotation != null) {
                val path =
                    annotation.arguments.firstOrNull { it.name?.asString() == "path" || it.name == null }?.value as? String
                        ?: ""
                return HttpMethodInfo(method, path)
            }
        }
        return null
    }

    private fun addRouteBlock(
        builder: FunSpec.Builder,
        function: KSFunctionDeclaration,
        methodInfo: HttpMethodInfo,
        implName: String
    ) {
        val routeFunc = MemberName("io.ktor.server.routing", methodInfo.type)
        
        builder.beginControlFlow("%M(%S) {", routeFunc, methodInfo.path)

        val args = mutableListOf<String>()

        function.parameters.forEach { param ->
            val paramName = param.name!!.asString()
            val type = param.type.resolve()
            val typeName = type.toTypeName()
            val qualifiedType = type.declaration.qualifiedName?.asString()

            val isPath = param.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == Path::class.qualifiedName }
            val isQuery = param.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == Query::class.qualifiedName }
            val isBody = param.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == Body::class.qualifiedName }

            if (isBody) {
                val receiveFunc = MemberName("io.ktor.server.request", "receive")
                builder.addStatement("val %L = call.%M<%T>()", paramName, receiveFunc, typeName)
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
                    builder.addStatement("val %L = call.%L[%S]?%L", paramName, accessor, paramKey, conversion)
                } else {
                    val httpStatusCode = HttpStatusCode::class.asClassName()
                    val respond = MemberName("io.ktor.server.response", "respond")

                    if (qualifiedType == "kotlin.String") {
                        if (isQuery) {
                            builder.addStatement("val %L = call.%L[%S] ?: \"\"", paramName, accessor, paramKey)
                        } else {
                            builder.addStatement(
                                "val %L = call.%L[%S] ?: return@%L call.%M(%T.BadRequest, \"Missing %L\")",
                                paramName, accessor, paramKey, methodInfo.type, respond, httpStatusCode, paramKey
                            )
                        }
                    } else {
                        builder.addStatement(
                            "val %L = call.%L[%S]?%L ?: return@%L call.%M(%T.BadRequest, \"Invalid %L\")",
                            paramName, accessor, paramKey, conversion, methodInfo.type, respond, httpStatusCode, paramKey
                        )
                    }
                }
                args.add(paramName)
            }
        }

        val respondFunc = MemberName("io.ktor.server.response", "respond")
        builder.addStatement("call.%M(%L.%L(%L))", respondFunc, implName, function.simpleName.asString(), args.joinToString(", "))

        builder.endControlFlow()
    }
}
