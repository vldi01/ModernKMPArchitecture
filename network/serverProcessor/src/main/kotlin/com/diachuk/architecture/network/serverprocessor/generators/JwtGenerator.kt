package com.diachuk.architecture.network.serverprocessor.generators

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

class JwtGenerator(private val codeGenerator: CodeGenerator) {

    fun generate(jwtClasses: List<KSClassDeclaration>) {
        if (jwtClasses.isEmpty()) return

        val packageName = "com.diachuk.architecture.network.server"
        val fileName = "JwtConfig"
        val fileSpec = FileSpec.builder(packageName, fileName)
        fileSpec.addImport("io.ktor.server.auth.jwt", "jwt")
        fileSpec.addImport("io.ktor.server.response", "respond")

        val jsonClass = ClassName("kotlinx.serialization.json", "Json")

        // Property: private val json = Json { ignoreUnknownKeys = true }
        fileSpec.addProperty(
            PropertySpec.builder("json", jsonClass)
                .addModifiers(KModifier.PRIVATE)
                .initializer("%T { ignoreUnknownKeys = true }", jsonClass)
                .build()
        )

        val funSpec = FunSpec.builder("configureJwt")
            .receiver(ClassName("io.ktor.server.auth", "AuthenticationConfig"))
            .addParameter("verifier", ClassName("com.auth0.jwt", "JWTVerifier"))

        // Add lambdas for customizing tokens: (UserToken) -> Any?
        jwtClasses.forEach { ksClass ->
            val paramName = ksClass.simpleName.asString().replaceFirstChar { it.lowercase() }
            val className = ksClass.toClassName()
            val lambdaType = LambdaTypeName.get(
                parameters = arrayOf(className),
                returnType = Any::class.asClassName().copy(nullable = true)
            ).copy(suspending = true)

            funSpec.addParameter(
                ParameterSpec.builder(paramName, lambdaType)
                    .defaultValue("{ it }")
                    .build()
            )
        }

        jwtClasses.forEach { ksClass ->
            addJwtBlock(funSpec, ksClass)
        }

        fileSpec.addFunction(funSpec.build())

        // Dependencies
        val sources = jwtClasses.mapNotNull { it.containingFile }.toTypedArray()
        fileSpec.build().writeTo(codeGenerator, Dependencies(true, *sources))
    }

    private fun addJwtBlock(funSpec: FunSpec.Builder, ksClass: KSClassDeclaration) {
        val name = ksClass.simpleName.asString()
        val paramName = name.replaceFirstChar { it.lowercase() }
        val decodedJwtClass = ClassName("com.auth0.jwt.interfaces", "DecodedJWT")
        val className = ksClass.toClassName()

        funSpec.beginControlFlow("jwt(%S)", name)
        funSpec.addStatement("verifier(verifier)")

        funSpec.beginControlFlow("validate { credential ->")

        // Token Instantiation Logic
        val isObject = ksClass.classKind == ClassKind.OBJECT
        val hasNoParams = ksClass.primaryConstructor?.parameters.isNullOrEmpty()

        if (isObject) {
            funSpec.addStatement("val token = %T", className)
        } else if (hasNoParams) {
            funSpec.addStatement("val token = %T()", className)
        } else {
            // Complex decoding logic
            funSpec.addStatement("val decodedJwt = credential.payload as? %T", decodedJwtClass)
            funSpec.addStatement("var payloadString: String? = null")

            funSpec.beginControlFlow("if (decodedJwt != null)")
            funSpec.addStatement("payloadString = String(java.util.Base64.getUrlDecoder().decode(decodedJwt.payload))")
            funSpec.nextControlFlow("else")
            // Fallback to manual parsing from header
            funSpec.addStatement("val authHeader = request.headers[\"Authorization\"]")
            funSpec.beginControlFlow("if (authHeader != null && authHeader.startsWith(\"Bearer \"))")
            funSpec.addStatement("val parts = authHeader.substring(7).trim().split('.')")
            funSpec.addStatement("if (parts.size == 3) payloadString = String(java.util.Base64.getUrlDecoder().decode(parts[1]))")
            funSpec.endControlFlow()
            funSpec.endControlFlow()

            funSpec.addStatement("if (payloadString == null) return@validate null")

            funSpec.beginControlFlow("val token = try")
            funSpec.addStatement("json.decodeFromString<%T>(payloadString)", className)
            funSpec.nextControlFlow("catch (e: Exception)")
            funSpec.addStatement("e.printStackTrace()")
            funSpec.addStatement("return@validate null")
            funSpec.endControlFlow()
        }

        // Apply customization lambda
        funSpec.addStatement("%L(token)", paramName)

        funSpec.endControlFlow() // End validate

        // Challenge block
        funSpec.beginControlFlow("challenge { _, _ ->")
        funSpec.addStatement(
            "call.respond(%T.Unauthorized, \"Token is not valid or has expired\")",
            ClassName("io.ktor.http", "HttpStatusCode")
        )
        funSpec.endControlFlow()

        funSpec.endControlFlow() // End jwt config
    }
}