package com.diachuk.modernarchitecture.features.a.logic

import com.diachuk.modernarchitecture.features.a.FeatureA
import org.koin.core.annotation.Factory

@Factory(binds = [FeatureA::class])
class FeatureAImpl : FeatureA {
    override fun doA() {
        println("Hello from feature A")
    }
}