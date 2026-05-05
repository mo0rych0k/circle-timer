package com.circle.timer.android.di

import com.circle.timer.android.BuildConfig
import com.circle.timer.common.app.info.AppEnvironment
import org.koin.core.module.Module
import org.koin.dsl.module

internal val androidAppModule: Module = module {
    single {
        AppEnvironment(
            isDebug = BuildConfig.DEBUG
        )
    }
}