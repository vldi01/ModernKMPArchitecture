package com.diachuk.modernarchitecture.navigaion.processor

import com.diachuk.modernarchitecture.navigaion.Screen
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate

class ScreenProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {
    private val codeGenerator: CodeGenerator = environment.codeGenerator
    private val logger: KSPLogger = environment.logger
    private val screenInjectorGenerator = ScreenInjectorGenerator(codeGenerator)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Screen::class.qualifiedName!!)

        val (validSymbols, invalidSymbols) = symbols.partition { it.validate() }

        logger.warn("ScreenProcessor found ${validSymbols.size} valid symbols.")

        validSymbols
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach { screenFunction ->
                try {
                    screenInjectorGenerator.generate(screenFunction)
                } catch (e: Exception) {
                    logger.error("Error generating injector for ${screenFunction.simpleName.asString()}: ${e.message}", screenFunction)
                }
            }

        return invalidSymbols
    }
}
