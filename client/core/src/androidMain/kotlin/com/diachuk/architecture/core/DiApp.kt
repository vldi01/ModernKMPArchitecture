package com.diachuk.architecture.core

import com.diachuk.client.database.DatabaseModuleAndroid
import org.koin.core.annotation.KoinApplication

@KoinApplication(modules = [DatabaseModuleAndroid::class])
actual object DiApp
