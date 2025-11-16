package com.diachuk.architecture.core

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.diachuk.modernarchitecture.navigaion.NavigationView
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        NavigationView(
            modifier = Modifier.fillMaxSize(),
        )
    }
}