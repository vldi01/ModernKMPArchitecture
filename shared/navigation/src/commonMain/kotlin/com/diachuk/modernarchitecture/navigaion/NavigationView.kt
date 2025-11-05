package com.diachuk.modernarchitecture.navigaion

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.currentKoinScope
import org.koin.compose.koinInject
import org.koin.core.scope.Scope


@Composable
inline fun <reified T> koinInjectAll(
    scope: Scope = currentKoinScope()
): List<T> {
    return remember(scope) {
        scope.getAll()
    }
}


@Composable
fun NavigationView(
    modifier: Modifier = Modifier
) {
    val navigator = koinInject<Navigator>()
    val graphInjectors = koinInjectAll<GraphInjector>()

    NavDisplay(
        modifier = modifier,
        backStack = navigator.backStack.collectAsState().value,
        onBack = { navigator.popBack() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider@{ key ->
            graphInjectors.firstNotNullOfOrNull { it.getNavEntry(key) }
                ?: NavEntry(object : Destination {}) { Text("Unknown route") }
        }
    )
}