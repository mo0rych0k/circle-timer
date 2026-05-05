package com.circle.timer.composeapp.di

import com.circle.timer.common.core.di.IsolatedKoinContext
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration


public fun initKoin(
    platformAppModules: List<Module> = emptyList(),
    additionalInitialization: KoinAppDeclaration = {}
) {
    IsolatedKoinContext.startKoin(
        modules = appModules + platformAppModules,
        additionalInitialization = additionalInitialization
    )
}

/** Avoid `init*` prefix — Swift/Obj-C interop treats those as initializers and may omit the symbol. */
public fun bootstrapPlatformKoin() {
    initKoin { }
}

