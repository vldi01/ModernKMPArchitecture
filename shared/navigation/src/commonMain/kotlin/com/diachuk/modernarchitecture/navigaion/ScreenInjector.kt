package com.diachuk.modernarchitecture.navigaion

import androidx.navigation3.runtime.NavEntry

interface ScreenInjector {
    fun getNavEntry(key: Destination): NavEntry<Destination>?
}