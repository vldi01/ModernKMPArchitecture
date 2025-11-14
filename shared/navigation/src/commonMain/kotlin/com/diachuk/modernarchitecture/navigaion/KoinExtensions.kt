package com.diachuk.modernarchitecture.navigaion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.compose.currentKoinScope
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.Kind
import org.koin.core.scope.Scope

@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified T> koinInjectNamedMap(
    scope: Scope = currentKoinScope()
): Map<String, T> {
    return remember(scope) {
        scope.getKoin().instanceRegistry.instances.values.map { it.beanDefinition }
            .filter { it.kind == Kind.Singleton }
            .filter { it.secondaryTypes.contains(ScreenInjector::class) }
            .filter { it.qualifier != null }
            .associate { it.qualifier!!.value to scope.get<ScreenInjector>(qualifier = it.qualifier) as T }
    }
}