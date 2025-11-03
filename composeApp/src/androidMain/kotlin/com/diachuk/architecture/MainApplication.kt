package com.diachuk.architecture

import android.app.Application
import com.diachuk.architecture.core.DiApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.ksp.generated.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        DiApp.startKoin {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}