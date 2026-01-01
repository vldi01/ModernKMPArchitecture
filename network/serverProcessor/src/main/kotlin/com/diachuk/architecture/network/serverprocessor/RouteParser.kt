package com.diachuk.architecture.network.serverprocessor

import com.diachuk.architecture.network.serverprocessor.models.AuthConfig
import com.diachuk.architecture.network.serverprocessor.models.EndpointDefinition
import com.diachuk.architecture.network.serverprocessor.models.EndpointParameter
import com.diachuk.architecture.network.serverprocessor.models.ParameterType
import com.diachuk.architecture.network.serverprocessor.models.ServiceDefinition
import com.diachuk.architecture.network.serverprocessor.utils.ProcessorConstants
import com.diachuk.architecture.network.serverprocessor.utils.findAnnotation
import com.diachuk.architecture.network.serverprocessor.utils.hasAnnotation
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class RouteParser(private val logger: KSPLogger) {

    fun parse(interfaceDecl: KSClassDeclaration, defaultJwtClass: KSClassDeclaration?): ServiceDefinition? {
        if (interfaceDecl.classKind != ClassKind.INTERFACE) {
            logger.error("Classes annotated with HTTP methods must be interfaces.", interfaceDecl)
            return null
        }

        val endpoints = interfaceDecl.getAllFunctions()
            .filter { it.isAbstract }
            .mapNotNull { parseEndpoint(it, defaultJwtClass) }
            .toList()

        return ServiceDefinition(
            packageName = interfaceDecl.packageName.asString(),
            interfaceName = interfaceDecl.simpleName.asString(),
            simpleName = interfaceDecl.simpleName.asString(),
            endpoints = endpoints,
            containingFile = interfaceDecl.containingFile!!
        )
    }

    private fun parseEndpoint(func: KSFunctionDeclaration, defaultJwtClass: KSClassDeclaration?): EndpointDefinition? {
        val methodInfo = getHttpMethod(func) ?: return null

        val parameters = func.parameters.map { parseParameter(it) }
        val authConfig = parseAuthConfig(func, defaultJwtClass)
        val isMultipart = func.hasAnnotation(ProcessorConstants.MULTIPART)

        return EndpointDefinition(
            name = func.simpleName.asString(),
            method = methodInfo.first,
            path = methodInfo.second,
            parameters = parameters,
            returnType = func.returnType?.resolve()?.toTypeName() ?: com.squareup.kotlinpoet.UNIT,
            authConfig = authConfig,
            isMultipart = isMultipart
        )
    }

    private fun parseParameter(param: KSValueParameter): EndpointParameter {
        val typeName = param.type.resolve().toTypeName()
        val name = param.name?.asString() ?: "arg"

        val type = when {
            param.hasAnnotation(ProcessorConstants.BODY) -> ParameterType.Body
            param.hasAnnotation(ProcessorConstants.QUERY_MAP) -> ParameterType.QueryMap
            param.hasAnnotation(ProcessorConstants.QUERY_NAME) -> ParameterType.QueryName
            else -> {
                val path = param.findAnnotation(ProcessorConstants.PATH)
                val query = param.findAnnotation(ProcessorConstants.QUERY)
                val part = param.findAnnotation(ProcessorConstants.PART)

                when {
                    path != null -> ParameterType.Path
                    query != null -> ParameterType.Query
                    part != null -> ParameterType.Part
                    else -> ParameterType.Body
                }
            }
        }

        // Extract value from annotation if present (e.g. @Query("search"))
        val explicitKey = when (type) {
            ParameterType.Path -> param.findAnnotation(ProcessorConstants.PATH)?.arguments?.firstOrNull()?.value as? String
            ParameterType.Query -> param.findAnnotation(ProcessorConstants.QUERY)?.arguments?.firstOrNull()?.value as? String
            ParameterType.Part -> param.findAnnotation(ProcessorConstants.PART)?.arguments?.firstOrNull()?.value as? String
            else -> null
        }

        // If annotation value is null or empty string, fall back to parameter name
        val keyName = if (!explicitKey.isNullOrBlank()) explicitKey else name

        return EndpointParameter(name, typeName, type, keyName)
    }

    private fun parseAuthConfig(func: KSFunctionDeclaration, defaultJwtClass: KSClassDeclaration?): AuthConfig {
        if (func.hasAnnotation(ProcessorConstants.NO_AUTH)) return AuthConfig.None

        val authAnnotation = func.findAnnotation(ProcessorConstants.AUTH_JWT)
        var targetClass: KSClassDeclaration? = null

        if (authAnnotation != null) {
            val arg = authAnnotation.arguments.firstOrNull { it.name?.asString() == "klass" }
                ?: authAnnotation.arguments.firstOrNull()
            val type = arg?.value as? KSType
            targetClass = type?.declaration as? KSClassDeclaration
        } else {
            targetClass = defaultJwtClass
        }

        return if (targetClass != null) {
            validateJwtClass(targetClass)
            AuthConfig.Required(
                securityName = targetClass.simpleName.asString(),
                principalClass = targetClass.toClassName()
            )
        } else {
            AuthConfig.None
        }
    }

    private fun validateJwtClass(decl: KSClassDeclaration) {
        val isValid = decl.hasAnnotation(ProcessorConstants.JWT_TYPE) ||
                decl.hasAnnotation(ProcessorConstants.DEFAULT_JWT_TYPE)

        if (!isValid) {
            logger.error("Class ${decl.simpleName.asString()} used in Auth must be annotated with @JwtType or @DefaultJwtType", decl)
        }
    }

    private fun getHttpMethod(function: KSFunctionDeclaration): Pair<String, String>? {
        val methods = mapOf(
            ProcessorConstants.GET to "get",
            ProcessorConstants.POST to "post",
            ProcessorConstants.PUT to "put",
            ProcessorConstants.DELETE to "delete",
            ProcessorConstants.HEAD to "head",
            ProcessorConstants.OPTIONS to "options",
            ProcessorConstants.PATCH to "patch"
        )

        for ((annotation, method) in methods) {
            val ann = function.findAnnotation(annotation)
            if (ann != null) {
                val path = ann.arguments.firstOrNull()?.value as? String ?: ""
                return method to path
            }
        }
        return null
    }
}