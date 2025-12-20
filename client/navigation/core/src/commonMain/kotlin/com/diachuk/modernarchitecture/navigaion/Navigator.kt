package com.diachuk.modernarchitecture.navigaion

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.Single

// Look for the usages to find where it is provided
typealias StartDestination = Destination

@Single
class Navigator(
    startDestination: StartDestination,
    coroutineScope: CoroutineScope
) {
    private val _backStack = MutableStateFlow(listOf(startDestination))
    val backStack = _backStack.asStateFlow()

    val canNavigateBack = backStack
        .map { it.size > 1 }
        .stateIn(
            scope = coroutineScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    fun navigate(destination: Destination) {
        _backStack.value += destination
    }

    fun popBack() {
        if (_backStack.value.size <= 1) return
        _backStack.value = _backStack.value.dropLast(1)
    }
}
