package com.diachuk.architecture.network.serverprocessor.generators

import com.diachuk.architecture.network.serverprocessor.models.AuthConfig
import com.diachuk.architecture.network.serverprocessor.models.EndpointDefinition
import com.diachuk.architecture.network.serverprocessor.models.EndpointParameter
import com.diachuk.architecture.network.serverprocessor.models.ParameterType
import com.diachuk.architecture.network.serverprocessor.models.ServiceDefinition
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.writeTo

class RouteGenerator(private val codeGenerator: CodeGenerator) {

    fun generate(service: ServiceDefinition) {
        val fileName = "${service.interfaceName}Routes"
        val fileSpec = FileSpec.builder(service.packageName, fileName)

        val routeClass = ClassName("io.ktor.server.routing", "Route")

        val funSpec = FunSpec.builder("bind${service.interfaceName}")
            .receiver(routeClass)
            .addParameter("impl", ClassName(service.packageName, service.interfaceName))

        service.endpoints.forEach { endpoint ->
            addEndpointBlock(funSpec, endpoint)
        }

        fileSpec.addFunction(funSpec.build())
        fileSpec.build().writeTo(codeGenerator, Dependencies(true, service.containingFile))
    }

    private fun addEndpointBlock(builder: FunSpec.Builder, endpoint: EndpointDefinition) {
        // 1. Auth Block
        if (endpoint.authConfig is AuthConfig.Required) {
            val authenticateFunc = MemberName("io.ktor.server.auth", "authenticate")
            builder.beginControlFlow("%M(%S) {", authenticateFunc, endpoint.authConfig.securityName)
        }

        // 2. Route Definition
        val routeFunc = MemberName("io.ktor.server.routing", endpoint.method)
        builder.beginControlFlow("%M(%S) {", routeFunc, endpoint.path)

        // 3. Parameter Parsing
        val callArgs = mutableListOf<String>()

        if (endpoint.isMultipart) {
            generateMultipartParsing(builder, endpoint.parameters, callArgs)
        } else {
            endpoint.parameters.forEach { param ->
                generateStandardParsing(builder, param, endpoint.method)
                callArgs.add(param.name)
            }
        }

        // 4. Invocation
        generateInvocation(builder, endpoint, callArgs)

        builder.endControlFlow() // End route

        if (endpoint.authConfig is AuthConfig.Required) {
            builder.endControlFlow() // End auth
        }
    }

    private fun generateStandardParsing(builder: FunSpec.Builder, param: EndpointParameter, method: String) {
        val effectiveKey = if (param.keyName == "KTORFIT_DEFAULT_VALUE") param.name else param.keyName

        when (param.type) {
            ParameterType.Body -> {
                val receiveFunc = MemberName("io.ktor.server.request", "receive")
                builder.addStatement("val %L = call.%M<%T>()", param.name, receiveFunc, param.typeName)
            }
            ParameterType.QueryMap -> {
                builder.addStatement(
                    "val %L = call.request.queryParameters.entries().associate { it.key to (it.value.firstOrNull() ?: \"\") }",
                    param.name
                )
            }
            ParameterType.QueryName -> {
                builder.addStatement("val %L = \"\"", param.name)
            }
            ParameterType.Path, ParameterType.Query -> {
                val accessor = if (param.type == ParameterType.Query) "request.queryParameters" else "parameters"
                val conversion = getConversionSuffix(param.typeName)

                if (param.typeName.isNullable) {
                    if (conversion.isEmpty()) {
                        builder.addStatement("val %L = call.%L[%S]", param.name, accessor, effectiveKey)
                    } else {
                        builder.addStatement("val %L = call.%L[%S]?%L", param.name, accessor, effectiveKey, conversion)
                    }
                } else {
                    val respond = MemberName("io.ktor.server.response", "respond")
                    val errorMessage = "Missing/Invalid $effectiveKey"
                    if (conversion.isEmpty()) {
                        builder.addStatement(
                            "val %L = call.%L[%S] ?: return@%L call.%M(%T.BadRequest, %S)",
                            param.name, accessor, effectiveKey, method, respond, ClassName("io.ktor.http", "HttpStatusCode"), errorMessage
                        )
                    } else {
                        builder.addStatement(
                            "val %L = call.%L[%S]?%L ?: return@%L call.%M(%T.BadRequest, %S)",
                            param.name, accessor, effectiveKey, conversion, method, respond, ClassName("io.ktor.http", "HttpStatusCode"), errorMessage
                        )
                    }
                }
            }
            else -> {}
        }
    }

    private fun generateMultipartParsing(builder: FunSpec.Builder, params: List<EndpointParameter>, callArgs: MutableList<String>) {
        val receiveMultipart = MemberName("io.ktor.server.request", "receiveMultipart")
        val partDataClass = ClassName("io.ktor.http.content", "PartData")

        params.forEach { param ->
            if (param.type == ParameterType.Part) {
                if (param.typeName.toString().startsWith("kotlin.collections.List")) {
                    builder.addStatement("val %L = mutableListOf<%T>()", param.name, partDataClass)
                } else {
                    builder.addStatement("var %L: String? = null", param.name)
                }
            }
            callArgs.add(param.name)
        }

        builder.addStatement("val multipart = call.%M()", receiveMultipart)
        builder.beginControlFlow("multipart.forEachPart { part ->")
        builder.beginControlFlow("when (part.name)")

        params.filter { it.type == ParameterType.Part }.forEach { param ->
            val effectiveKey = if (param.keyName == "KTORFIT_DEFAULT_VALUE") param.name else param.keyName
            builder.beginControlFlow("%S ->", effectiveKey)
            if (param.typeName.toString().startsWith("kotlin.collections.List")) {
                builder.addStatement("%L.add(part)", param.name)
            } else {
                builder.addStatement("if (part is %T.FormItem) %L = part.value", partDataClass, param.name)
                builder.addStatement("part.dispose()")
            }
            builder.endControlFlow()
        }

        builder.endControlFlow()
        builder.endControlFlow()
    }

    private fun generateInvocation(builder: FunSpec.Builder, endpoint: EndpointDefinition, callArgs: List<String>) {
        val respondFunc = MemberName("io.ktor.server.response", "respond")
        val callContextClass = ClassName("com.diachuk.architecture.network.core", "CallContext")
        val withContextFunc = MemberName("kotlinx.coroutines", "withContext")

        if (endpoint.authConfig is AuthConfig.Required) {
            val principalFunc = MemberName("io.ktor.server.auth", "principal")

            builder.addStatement(
                "val principal = call.%M<%T>() ?: return@%L call.%M(%T.Unauthorized)",
                principalFunc, endpoint.authConfig.principalClass, endpoint.method, respondFunc, ClassName("io.ktor.http", "HttpStatusCode")
            )
        } else {
            // Using Unit as the token when no auth is present
            // This satisfies CallContext<TOKEN : Any>
            builder.addStatement("val principal = Unit")
        }

        // Always wrap the implementation call in withContext(CallContext(...))
        builder.beginControlFlow("%M(%T(call, principal))", withContextFunc, callContextClass)

        builder.addStatement(
            "call.%M(impl.%L(%L))",
            respondFunc,
            endpoint.name,
            callArgs.joinToString(", ")
        )

        builder.endControlFlow() // End withContext
    }

    private fun getConversionSuffix(typeName: TypeName): String {
        return when (typeName.toString()) {
            "kotlin.Long", "kotlin.Long?" -> ".toLongOrNull()"
            "kotlin.Int", "kotlin.Int?" -> ".toIntOrNull()"
            "kotlin.Boolean", "kotlin.Boolean?" -> ".toBooleanStrictOrNull()"
            "kotlin.Double", "kotlin.Double?" -> ".toDoubleOrNull()"
            "kotlin.Float", "kotlin.Float?" -> ".toFloatOrNull()"
            else -> ""
        }
    }
}