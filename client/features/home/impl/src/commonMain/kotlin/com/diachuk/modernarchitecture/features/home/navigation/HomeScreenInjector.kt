package com.diachuk.modernarchitecture.features.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.diachuk.modernarchitecture.features.home.api.HomeDestination
import com.diachuk.modernarchitecture.features.home.logic.HomeViewModel
import com.diachuk.modernarchitecture.features.home.ui.HomeScreen
import com.diachuk.modernarchitecture.navigaion.Destination
import com.diachuk.modernarchitecture.navigaion.ScreenInjector
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.Single

@Single
class HomeScreenInjector : ScreenInjector {
    override fun injectInto(scope: EntryProviderScope<Destination>) {
        scope.entry<HomeDestination> {
            HomeScreen(koinViewModel<HomeViewModel>())
        }
    }
}
