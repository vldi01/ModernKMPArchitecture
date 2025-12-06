package com.diachuk.modernarchitecture.features.b

import androidx.navigation3.runtime.NavEntry
import com.diachuk.modernarchitecture.navigaion.Destination
import com.diachuk.modernarchitecture.navigaion.ScreenInjector
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
@Named(type = DestinationB::class)
class BScreenInjector : ScreenInjector {
    override fun getNavEntry(key: Destination): NavEntry<Destination>? {
        if (key !is DestinationB) return null

        return NavEntry(key) {
            BScreen()
        }
    }
}