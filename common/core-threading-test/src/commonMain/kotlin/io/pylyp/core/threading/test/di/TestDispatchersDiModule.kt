package com.circle.timer.core.threading.test.di

import com.circle.timer.core.threading.DispatcherProvider
import com.circle.timer.core.threading.test.TestDispatcherProviderImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public val testDispatchersModule: Module = module {
    singleOf(::TestDispatcherProviderImpl) bind DispatcherProvider::class
}