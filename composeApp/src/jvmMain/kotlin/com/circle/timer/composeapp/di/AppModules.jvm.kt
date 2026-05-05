package com.circle.timer.composeapp.di


import com.circle.timer.common.app.info.AppEnvironment
import org.koin.core.module.Module
import org.koin.dsl.module


internal actual val platformModule: Module = module {
    single {
        AppEnvironment(
//            false only for JVM
            isDebug = false
        )
    }
}
