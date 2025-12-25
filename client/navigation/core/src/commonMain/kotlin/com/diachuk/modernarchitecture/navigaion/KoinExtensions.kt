package com.diachuk.modernarchitecture.navigaion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.compose.currentKoinScope
import org.koin.core.scope.Scope

@Composable
inline fun <reified T> koinGetAll(
    scope: Scope = currentKoinScope()
): List<T> {
    return remember(scope) {
        scope.getKoin().getAll<T>()
    }
}