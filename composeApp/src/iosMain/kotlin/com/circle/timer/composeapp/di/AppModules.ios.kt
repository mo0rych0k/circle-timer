package com.circle.timer.composeapp.di


import com.circle.timer.common.app.info.AppEnvironment
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.experimental.ExperimentalNativeApi


@OptIn(ExperimentalNativeApi::class)
internal actual val platformModule: Module = module {
    single {
        AppEnvironment(
            isDebug = Platform.isDebugBinary
        )
    }
}
