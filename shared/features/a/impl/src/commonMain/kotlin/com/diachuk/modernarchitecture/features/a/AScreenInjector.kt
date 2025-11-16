package com.diachuk.modernarchitecture.features.a

import androidx.navigation3.runtime.NavEntry
import com.diachuk.modernarchitecture.navigaion.Destination
import com.diachuk.modernarchitecture.navigaion.ScreenInjector
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import org.koin.core.parameter.parametersOf

@Single
@Named(type = DestinationA::class)
class AScreenInjector : ScreenInjector {
    override fun getNavEntry(key: Destination): NavEntry<Destination>? {
        if (key !is DestinationA) return null

        return NavEntry(key) {
            AScreen(key, koinViewModel { parametersOf(key) })
        }
    }
}