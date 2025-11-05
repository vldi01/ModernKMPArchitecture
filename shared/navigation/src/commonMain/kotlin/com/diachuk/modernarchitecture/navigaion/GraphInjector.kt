package com.diachuk.modernarchitecture.navigaion

import androidx.navigation3.runtime.NavEntry

interface GraphInjector {
    fun getNavEntry(key: Destination): NavEntry<Destination>?
}