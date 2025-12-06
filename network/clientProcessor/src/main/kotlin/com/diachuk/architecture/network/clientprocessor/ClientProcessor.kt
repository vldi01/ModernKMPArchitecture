package com.diachuk.architecture.network.clientprocessor

import com.diachuk.architecture.network.core.Body
import com.diachuk.architecture.network.core.DELETE
import com.diachuk.architecture.network.core.GET
import com.diachuk.architecture.network.core.POST
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
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import io.ktor.client.HttpClient
import io.ktor.http.ContentType

class ClientProcessor(
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
            generateClient(interfaceDecl)
        }

        return symbols.filterNot { it.validate() }.toList()
    }

    private fun generateClient(interfaceDecl: KSClassDeclaration) {
        val packageName = interfaceDecl.packageName.asString()
        val interfaceName = interfaceDecl.simpleName.asString()
        val className = "${interfaceName}Client"

        val fileSpec = FileSpec.builder(packageName, className)

        val httpClient = HttpClient::class.asClassName()

        val classBuilder = TypeSpec.classBuilder(className)
            .addModifiers(KModifier.INTERNAL)
            .addSuperinterface(interfaceDecl.asStarProjectedType().toTypeName())
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("client", httpClient)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("client", httpClient)
                    .initializer("client")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )

        interfaceDecl.getAllFunctions().forEach { function ->
            if (function.isAbstract) {
                val httpMethod = getHttpMethod(function)
                if (httpMethod != null) {
                    classBuilder.addFunction(generateFunction(function, httpMethod))
                }
            }
        }

        fileSpec.addType(classBuilder.build())
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

    private fun generateFunction(
        function: KSFunctionDeclaration,
        methodInfo: HttpMethodInfo
    ): FunSpec {
        val builder = FunSpec.builder(function.simpleName.asString())
            .addModifiers(KModifier.OVERRIDE)
            .addModifiers(KModifier.SUSPEND)
            .returns(function.returnType!!.resolve().toTypeName())

        function.parameters.forEach { param ->
            builder.addParameter(param.name!!.asString(), param.type.resolve().toTypeName())
        }

        val url = methodInfo.path.replace(Regex("\\{([^}]+)\\}")) { matchResult ->
            "$" + matchResult.groupValues[1]
        }

        val requestMethod = MemberName("io.ktor.client.request", methodInfo.type)

        builder.addCode("return client.%M(%P)", requestMethod, url)

        val hasBodyOrQuery = function.parameters.any { param ->
            param.annotations.any {
                val name = it.annotationType.resolve().declaration.qualifiedName?.asString()
                name == Body::class.qualifiedName || name == Query::class.qualifiedName
            }
        }

        if (hasBodyOrQuery) {
            builder.beginControlFlow(" {")

            function.parameters.forEach { param ->
                val isBody =
                    param.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == Body::class.qualifiedName }
                if (isBody) {
                    val contentType = ContentType::class.asClassName()
                    val contentTypeFunc = MemberName("io.ktor.http", "contentType")
                    val setBodyFunc = MemberName("io.ktor.client.request", "setBody")

                    builder.addStatement("%M(%T.Application.Json)", contentTypeFunc, contentType)
                    builder.addStatement("%M(%L)", setBodyFunc, param.name!!.asString())
                }
            }

            function.parameters.forEach { param ->
                val queryAnnotation =
                    param.annotations.find { it.annotationType.resolve().declaration.qualifiedName?.asString() == Query::class.qualifiedName }
                if (queryAnnotation != null) {
                    val queryName = param.name!!.asString()
                    val parameterFunc = MemberName("io.ktor.client.request", "parameter")
                    builder.addStatement(
                        "%M(%S, %L)",
                        parameterFunc,
                        queryName,
                        param.name!!.asString()
                    )
                }
            }

            builder.endControlFlow()
        }

        val bodyFunc = MemberName("io.ktor.client.call", "body")
        builder.addCode(".%M()", bodyFunc)

        return builder.build()
    }
}
