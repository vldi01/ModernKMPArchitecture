package com.diachuk.architecture.network.serverprocessor

import com.auth0.jwt.JWTVerifier
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.jwt.JWTCredential

class JwtGenerator(private val codeGenerator: CodeGenerator) {

    fun generate(jwtClasses: List<KSClassDeclaration>) {
        if (jwtClasses.isEmpty()) return

        val packageName = "com.diachuk.architecture.network.server"
        val fileName = "JwtConfig"

        val fileSpec = FileSpec.builder(packageName, fileName)

        val authConfigClass = AuthenticationConfig::class.asClassName()
        val jwtVerifierClass = JWTVerifier::class.asClassName()
        val jwtCredentialClass = JWTCredential::class.asClassName()
        val jwtFunc = MemberName("io.ktor.server.auth.jwt", "jwt")
        val respondFunc = MemberName("io.ktor.server.response", "respond")
        val httpStatusCodeClass = HttpStatusCode::class.asClassName()
        val decodedJwtClass = ClassName("com.auth0.jwt.interfaces", "DecodedJWT")
        val jsonClass = ClassName("kotlinx.serialization.json", "Json")
        val decodeFromStringMember = MemberName("kotlinx.serialization", "decodeFromString")

        // Create a private Json property
        val jsonProperty = PropertySpec.builder("json", jsonClass)
            .addModifiers(KModifier.PRIVATE)
            .initializer("%T { ignoreUnknownKeys = true }", jsonClass)
            .build()

        fileSpec.addProperty(jsonProperty)

        val funSpec = FunSpec.builder("configureJwt")
            .receiver(authConfigClass)
            .addParameter("verifier", jwtVerifierClass)

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
            val name = ksClass.simpleName.asString()
            val paramName = name.replaceFirstChar { it.lowercase() }

            funSpec.beginControlFlow("%M(%S)", jwtFunc, name)
            funSpec.addStatement("verifier(verifier)")

            funSpec.beginControlFlow("validate { credential ->")

            val isObject = ksClass.classKind == ClassKind.OBJECT
            val hasNoParams = ksClass.primaryConstructor?.parameters.isNullOrEmpty()

            if (isObject) {
                funSpec.addStatement("val token = %T", ksClass.toClassName())
            } else if (hasNoParams) {
                funSpec.addStatement("val token = %T()", ksClass.toClassName())
            } else {
                funSpec.addStatement("val decodedJwt = credential.payload as? %T", decodedJwtClass)
                funSpec.beginControlFlow("if (decodedJwt == null)")
                funSpec.addStatement("return@validate null")
                funSpec.endControlFlow()

                funSpec.addStatement("val payloadString = String(java.util.Base64.getUrlDecoder().decode(decodedJwt.payload))")

                funSpec.beginControlFlow("val token = try")
                // Use the private json property
                funSpec.addStatement(
                    "json.%M<%T>(payloadString)",
                    decodeFromStringMember,
                    ksClass.toClassName()
                )
                funSpec.nextControlFlow("catch (e: Exception)")
                funSpec.addStatement("e.printStackTrace()")
                funSpec.addStatement("return@validate null")
                funSpec.endControlFlow()
            }

            funSpec.addStatement("%L(token)", paramName)

            funSpec.endControlFlow() // validate

            funSpec.beginControlFlow("challenge { _, _ ->")
            funSpec.addStatement(
                "call.%M(%T.Unauthorized, \"Token is not valid or has expired\")",
                respondFunc,
                httpStatusCodeClass
            )
            funSpec.endControlFlow() // challenge

            funSpec.endControlFlow() // jwt block
        }

        fileSpec.addFunction(funSpec.build())

        val sources = jwtClasses.mapNotNull { it.containingFile }.toTypedArray()
        fileSpec.build().writeTo(codeGenerator, Dependencies(true, *sources))
    }
}
