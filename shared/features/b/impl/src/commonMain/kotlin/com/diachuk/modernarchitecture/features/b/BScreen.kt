package com.diachuk.modernarchitecture.features.b

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.diachuk.modernarchitecture.navigaion.Screen


@Screen(DestinationB::class)
@Composable
fun BScreen() {
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