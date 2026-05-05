package com.circle.timer.features.timer.domain.usecase

import com.circle.timer.features.timer.domain.TimerSettings
import com.circle.timer.features.timer.domain.TimerSettingsRepository

public class GetTimerSettingsUseCase(
    private val repository: TimerSettingsRepository,
) {
    public suspend operator fun invoke(): TimerSettings = repository.getTimerSettings()
}
