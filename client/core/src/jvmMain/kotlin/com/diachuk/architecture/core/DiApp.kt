package com.diachuk.architecture.core

import com.diachuk.client.database.DatabaseModuleJvm
import org.koin.core.annotation.KoinApplication

@KoinApplication(modules = [DatabaseModuleJvm::class])
actual object DiApp
