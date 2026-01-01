package com.diachuk.architecture.network.serverprocessor

import com.diachuk.architecture.network.core.DefaultJwtType
import com.diachuk.architecture.network.core.JwtType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HEAD
import de.jensklingenberg.ktorfit.http.OPTIONS
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT

class ServerProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val routeGenerator = RouteGenerator(codeGenerator)
    private val jwtGenerator = JwtGenerator(codeGenerator)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = (
                resolver.getSymbolsWithAnnotation(GET::class.qualifiedName!!) +
                        resolver.getSymbolsWithAnnotation(POST::class.qualifiedName!!) +
                        resolver.getSymbolsWithAnnotation(PUT::class.qualifiedName!!) +
                        resolver.getSymbolsWithAnnotation(DELETE::class.qualifiedName!!) +
                        resolver.getSymbolsWithAnnotation(HEAD::class.qualifiedName!!) +
                        resolver.getSymbolsWithAnnotation(OPTIONS::class.qualifiedName!!) +
                        resolver.getSymbolsWithAnnotation(PATCH::class.qualifiedName!!)
                ).distinct()

        val validSymbols = symbols.filter { it.validate() }.toList()

        val defaultJwtSymbols = resolver.getSymbolsWithAnnotation(DefaultJwtType::class.qualifiedName!!).toList()
        if (defaultJwtSymbols.size > 1) {
            throw IllegalArgumentException("Only one class can be annotated with @DefaultJwtType")
        }
        val defaultJwtClass = defaultJwtSymbols.firstOrNull() as? KSClassDeclaration
        if (defaultJwtSymbols.isNotEmpty() && defaultJwtClass == null) {
            logger.error("@DefaultJwtType must be applied to a class", defaultJwtSymbols.first())
        }

        val interfaces = validSymbols.filterIsInstance<KSFunctionDeclaration>()
            .mapNotNull { it.parentDeclaration as? KSClassDeclaration }
            .distinct()

        interfaces.forEach { interfaceDecl ->
            routeGenerator.generateServerRoute(interfaceDecl, defaultJwtClass)
        }

        val jwtSymbols = resolver.getSymbolsWithAnnotation(JwtType::class.qualifiedName!!).toList()
        val allJwtSymbols = (jwtSymbols + defaultJwtSymbols).distinct()
        val validJwtSymbols = allJwtSymbols.filter { it.validate() }
        val jwtClasses = validJwtSymbols.filterIsInstance<KSClassDeclaration>()

        jwtGenerator.generate(jwtClasses)

        return (symbols.filterNot { it.validate() } + allJwtSymbols.filterNot { it.validate() }).distinct()
            .toList()
    }
}
