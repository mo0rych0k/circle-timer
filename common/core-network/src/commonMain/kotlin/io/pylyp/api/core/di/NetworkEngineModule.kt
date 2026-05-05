package com.circle.timer.api.core.di

import com.circle.timer.api.core.client.provideHttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module

public val networkEngineModule: Module = module {
    single { provideHttpClientEngine().create() }
}