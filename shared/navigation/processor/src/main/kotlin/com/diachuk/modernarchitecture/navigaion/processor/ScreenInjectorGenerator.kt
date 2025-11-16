package com.diachuk.modernarchitecture.navigaion.processor

import androidx.navigation3.runtime.NavEntry
import com.diachuk.modernarchitecture.navigaion.Destination
import com.diachuk.modernarchitecture.navigaion.ScreenInjector
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.joinToCode
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

class ScreenInjectorGenerator(private val codeGenerator: CodeGenerator) {

    fun generate(screenFunction: KSFunctionDeclaration) {
        val screenAnnotation = screenFunction.annotations.first {
            it.shortName.asString() == "Screen"
        }
        val destinationType = screenAnnotation.arguments
            .first { it.name?.asString() == "destination" }.value as KSType
        val destinationClassName = destinationType.toClassName()

        val screenPackage = screenFunction.packageName.asString()
        val screenName = screenFunction.simpleName.asString()
        val injectorClassName = "${screenName}Injector"

        val (destinationParam, viewModelParam) = processParameters(screenFunction, destinationType)

        val fileSpec = FileSpec.builder(screenPackage, injectorClassName)
            .addType(
                TypeSpec.classBuilder(injectorClassName)
                    .addSuperinterface(ScreenInjector::class)
                    .addAnnotation(Single::class.asClassName())
                    .addAnnotation(
                        AnnotationSpec.builder(Named::class)
                            .addMember("type = %T::class", destinationClassName)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("getNavEntry")
                            .addModifiers(KModifier.OVERRIDE)
                            .addParameter("key", Destination::class.asClassName())
                            .returns(
                                NavEntry::class.asClassName().parameterizedBy(
                                    Destination::class.asClassName()
                                ).copy(nullable = true)
                            )
                            .addCode(buildCodeBlock {
                                addStatement("if (key !is %T) return null", destinationClassName)
                                addStatement("")
                                addStatement("return NavEntry(key) {")
                                indent()
                                addStatement(
                                    "%L(%L)",
                                    screenName,
                                    buildScreenCallArgs(destinationParam, viewModelParam)
                                        .joinToCode(", ")
                                )
                                unindent()
                                addStatement("}")
                            })
                            .build()
                    )
                    .build()
            )
            .build()

        fileSpec.writeTo(codeGenerator, false)
    }

    private fun processParameters(
        screenFunction: KSFunctionDeclaration,
        destinationType: KSType
    ): Pair<KSValueParameter?, KSValueParameter?> {
        val parameters = screenFunction.parameters

        var destinationParam: KSValueParameter? = null
        var viewModelParam: KSValueParameter? = null

        parameters.forEach { param ->
            val paramType = param.type.resolve()
            when {
                paramType == destinationType -> destinationParam = param
                paramType.declaration.qualifiedName?.asString()
                    ?.endsWith("ViewModel") == true -> viewModelParam = param

                else -> throw IllegalArgumentException(
                    "Screen ${screenFunction.simpleName.asString()} has an unsupported parameter: ${param.name?.asString()} of type ${paramType.toClassName().simpleName}. " +
                            "Only a Destination and a ViewModel are supported."
                )
            }
        }
        return Pair(destinationParam, viewModelParam)
    }

    private fun buildScreenCallArgs(
        destinationParam: KSValueParameter?,
        viewModelParam: KSValueParameter?
    ): List<CodeBlock> {
        val args = mutableListOf<CodeBlock>()
        destinationParam?.let {
            args.add(CodeBlock.of("%N = key", it.name!!.asString()))
        }
        viewModelParam?.let {
            val koinViewModelMember =
                MemberName("org.koin.compose.viewmodel", "koinViewModel")
            val parametersOfMember =
                MemberName("org.koin.core.parameter", "parametersOf")

            val koinCall = if (destinationParam != null) {
                CodeBlock.builder()
                    .add("%M { %M(key) }", koinViewModelMember, parametersOfMember)
                    .build()
            } else {
                CodeBlock.builder()
                    .add("%M()", koinViewModelMember)
                    .build()
            }
            args.add(CodeBlock.builder().add("%N = ", it.name!!.asString()).add(koinCall).build())
        }
        return args
    }
}
