package com.circle.timer.api.core.di

import com.circle.timer.api.core.client.createHttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

public val networkModule: Module = module {
    single { createHttpClient(get()) }
}