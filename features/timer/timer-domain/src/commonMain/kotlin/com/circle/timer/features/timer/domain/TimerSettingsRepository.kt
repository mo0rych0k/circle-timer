package com.circle.timer.features.timer.domain

public interface TimerSettingsRepository {
    public suspend fun getTimerSettings(): TimerSettings
    public suspend fun saveTimerSettings(settings: TimerSettings)
    public suspend fun isOnboardingCompleted(): Boolean
    public suspend fun setOnboardingCompleted(completed: Boolean)
}
