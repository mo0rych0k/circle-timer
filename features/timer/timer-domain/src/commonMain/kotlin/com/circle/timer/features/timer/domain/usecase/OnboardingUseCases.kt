package com.circle.timer.features.timer.domain.usecase

import com.circle.timer.features.timer.domain.TimerSettingsRepository

public class IsOnboardingCompletedUseCase(
    private val repository: TimerSettingsRepository,
) {
    public suspend operator fun invoke(): Boolean = repository.isOnboardingCompleted()
}

public class CompleteOnboardingUseCase(
    private val repository: TimerSettingsRepository,
) {
    public suspend operator fun invoke() {
        repository.setOnboardingCompleted(completed = true)
    }
}
