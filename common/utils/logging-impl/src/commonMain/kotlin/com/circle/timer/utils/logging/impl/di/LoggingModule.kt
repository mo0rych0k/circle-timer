package com.circle.timer.utils.logging.impl.di

import com.circle.timer.utils.logging.LoggerInterface
import com.circle.timer.utils.logging.impl.LoggerImpl
import org.koin.core.module.Module
import org.koin.dsl.module

public val loggingModule: Module = module {
    single<LoggerInterface> { LoggerImpl(environment = get()) }
}
