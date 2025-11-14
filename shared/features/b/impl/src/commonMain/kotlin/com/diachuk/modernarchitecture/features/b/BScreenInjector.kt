package com.diachuk.modernarchitecture.features.b

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
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
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                Column(
                    modifier = Modifier.safeContentPadding()
                ) {
                    Text("HELLO")
                }
            }
        }
    }
}