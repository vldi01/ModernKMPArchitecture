package com.diachuk.architecture

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.diachuk.architecture.core.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ModernArchitecture",
    ) {
        App()
    }
}