package com.circle.timer.features.timer.domain.di

import com.circle.timer.features.timer.domain.usecase.CompleteOnboardingUseCase
import com.circle.timer.features.timer.domain.usecase.GetTimerSettingsUseCase
import com.circle.timer.features.timer.domain.usecase.IsOnboardingCompletedUseCase
import com.circle.timer.features.timer.domain.usecase.SaveTimerSettingsUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

public val timerDomainModule: Module = module {
    factoryOf(::GetTimerSettingsUseCase)
    factoryOf(::SaveTimerSettingsUseCase)
    factoryOf(::IsOnboardingCompletedUseCase)
    factoryOf(::CompleteOnboardingUseCase)
}
