package com.circle.timer.android

import android.app.Application
import com.circle.timer.android.di.androidAppModule
import com.circle.timer.composeapp.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class KmpMviApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(platformAppModules = listOf(androidAppModule)) {
            androidLogger()
            androidContext(this@KmpMviApplication)
        }
    }
}

