package com.circle.timer.composeapp.di

import com.circle.timer.api.core.di.networkEngineModule
import com.circle.timer.api.core.di.networkModule
import com.circle.timer.common.core.persistence.di.persistenceDatabaseModule
import com.circle.timer.common.core.persistence.di.persistenceDatabasePlatformModule
import com.circle.timer.core.threading.di.coreThreadingModule
import com.circle.timer.features.onboarding.ui.di.onboardingUiModule
import com.circle.timer.features.timer.data.di.timerDataModule
import com.circle.timer.features.timer.domain.di.timerDomainModule
import com.circle.timer.features.timer.ui.di.timerUiModule
import org.koin.core.module.Module

internal val appModules: List<Module> = listOf(
    networkModule,
    networkEngineModule,
    appNavigationModule,
    coreThreadingModule,
    persistenceDatabaseModule,
    persistenceDatabasePlatformModule,

    onboardingUiModule,
    timerDataModule,
    timerDomainModule,
    timerUiModule,
    platformModule,
)

internal expect val platformModule: Module
