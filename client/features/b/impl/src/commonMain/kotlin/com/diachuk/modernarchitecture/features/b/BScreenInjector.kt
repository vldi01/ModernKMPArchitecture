package com.diachuk.modernarchitecture.features.b

import androidx.navigation3.runtime.EntryProviderScope
import com.diachuk.modernarchitecture.navigaion.Destination
import com.diachuk.modernarchitecture.navigaion.ScreenInjector
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class BScreenInjector : ScreenInjector {
    override fun injectInto(scope: EntryProviderScope<Destination>) {
        scope.entry<DestinationB> { BScreen() }
    }
}