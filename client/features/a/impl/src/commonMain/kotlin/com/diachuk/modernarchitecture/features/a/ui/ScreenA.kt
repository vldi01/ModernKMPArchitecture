package com.diachuk.modernarchitecture.features.a.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.diachuk.modernarchitecture.features.a.DestinationA
import com.diachuk.modernarchitecture.features.a.logic.AViewModel
import com.diachuk.modernarchitecture.features.b.DestinationB
import com.diachuk.modernarchitecture.navigaion.Navigator
import org.koin.compose.koinInject

@Composable
fun AScreen(destination: DestinationA, vm: AViewModel) {
    UiA()
}

@Composable
private fun UiA() {
    val navigator = koinInject<Navigator>()
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.safeContentPadding()
        ) {
            Text(text = "Hello world from Screen A")
            Button(onClick = {
                navigator.navigate(DestinationB)
            }) {
                Text("Go to B")
            }
        }
    }
}
