package com.diachuk.modernarchitecture.navigaion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.compose.currentKoinScope
import org.koin.core.definition.Kind
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

@Composable
inline fun <reified T> koinGetAll(
    scope: Scope = currentKoinScope()
): List<T> {
    return remember(scope) {
        scope.getKoin().getAll<T>()
    }
}