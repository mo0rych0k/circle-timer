package com.circle.timer.core.threading.di

import com.circle.timer.core.threading.DispatcherProvider
import com.circle.timer.core.threading.DispatcherProviderImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public val coreThreadingModule: Module =
    module {
        singleOf(::DispatcherProviderImpl) bind DispatcherProvider::class
    }