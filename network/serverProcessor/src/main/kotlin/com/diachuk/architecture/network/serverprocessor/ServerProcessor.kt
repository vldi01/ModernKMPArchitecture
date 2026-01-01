package com.diachuk.architecture.network.serverprocessor

import com.diachuk.architecture.network.serverprocessor.generators.JwtGenerator
import com.diachuk.architecture.network.serverprocessor.generators.RouteGenerator
import com.diachuk.architecture.network.serverprocessor.utils.ProcessorConstants
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate

class ServerProcessor(
    codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val routeGenerator = RouteGenerator(codeGenerator)
    private val routeParser = RouteParser(logger)
    private val jwtGenerator = JwtGenerator(codeGenerator)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val methods = listOf(
            ProcessorConstants.GET, ProcessorConstants.POST, ProcessorConstants.PUT,
            ProcessorConstants.DELETE, ProcessorConstants.HEAD, ProcessorConstants.OPTIONS,
            ProcessorConstants.PATCH
        )

        // 1. Collect all symbols annotated with HTTP methods
        val routeSymbols = methods.flatMap { resolver.getSymbolsWithAnnotation(it) }.distinct()

        // 2. Identify Default JWT
        val defaultJwtSymbols = resolver.getSymbolsWithAnnotation(ProcessorConstants.DEFAULT_JWT_TYPE).toList()
        if (defaultJwtSymbols.size > 1) {
            logger.error("Only one class can be annotated with @DefaultJwtType", defaultJwtSymbols[1])
        }
        val defaultJwtClass = defaultJwtSymbols.firstOrNull() as? KSClassDeclaration

        // 3. Process Routes
        // Filter for valid symbols and group by parent interface
        val interfaces = routeSymbols
            .filter { it.validate() }
            .filterIsInstance<KSFunctionDeclaration>()
            .mapNotNull { it.parentDeclaration as? KSClassDeclaration }
            .distinct()

        interfaces.forEach { interfaceDecl ->
            val serviceDef = routeParser.parse(interfaceDecl, defaultJwtClass)
            if (serviceDef != null) {
                routeGenerator.generate(serviceDef)
            }
        }

        // 4. Process JWTs
        val jwtSymbols = resolver.getSymbolsWithAnnotation(ProcessorConstants.JWT_TYPE).toList()
        val allJwtSymbols = (jwtSymbols + defaultJwtSymbols).distinct().filterIsInstance<KSClassDeclaration>()

        if (allJwtSymbols.isNotEmpty()) {
             // We pass all valid JWT symbols to the generator
             jwtGenerator.generate(allJwtSymbols.filter { it.validate() })
        }

        // Return invalid symbols to be processed in the next round
        return (routeSymbols + allJwtSymbols).filterNot { it.validate() }.toList()
    }
}