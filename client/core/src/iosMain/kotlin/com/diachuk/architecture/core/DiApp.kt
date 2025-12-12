package com.diachuk.architecture.core

import com.diachuk.client.database.DatabaseModuleIos
import org.koin.core.annotation.KoinApplication

@KoinApplication(modules = [DatabaseModuleIos::class])
actual object DiApp
