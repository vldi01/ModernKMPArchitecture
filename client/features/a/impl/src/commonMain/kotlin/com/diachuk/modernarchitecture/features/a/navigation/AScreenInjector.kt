package com.diachuk.modernarchitecture.features.a.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.diachuk.modernarchitecture.features.a.DestinationA
import com.diachuk.modernarchitecture.features.a.ui.AScreen
import com.diachuk.modernarchitecture.navigaion.Destination
import com.diachuk.modernarchitecture.navigaion.ScreenInjector
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.Single
import org.koin.core.parameter.parametersOf

@Single
class AScreenInjector : ScreenInjector {
    override fun injectInto(scope: EntryProviderScope<Destination>) {
        scope.entry<DestinationA> { AScreen(it, koinViewModel { parametersOf(it) }) }
    }
}
