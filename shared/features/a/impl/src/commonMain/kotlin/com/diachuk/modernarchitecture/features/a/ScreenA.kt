package com.diachuk.modernarchitecture.features.a

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.diachuk.modernarchitecture.features.b.DestinationB
import com.diachuk.modernarchitecture.navigaion.Navigator
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScreenA(vm: AViewModel = koinViewModel()) {
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