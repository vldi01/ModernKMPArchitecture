package com.diachuk.modernarchitecture.navigaion

import androidx.navigation3.runtime.EntryProviderScope

interface ScreenInjector {
    fun injectInto(scope: EntryProviderScope<Destination>)
}