package com.circle.timer.features.timer.data

import com.circle.timer.features.timer.domain.TimerSettings
import com.circle.timer.features.timer.domain.TimerSettingsRepository
import com.circle.timer.features.timer.domain.normalizeBreakDuration
import com.circle.timer.features.timer.domain.normalizeDuration
import com.circle.timer.features.timer.domain.normalizeIntervals
import com.russhwolf.settings.Settings

internal class TimerSettingsRepositoryImpl(
    private val settings: Settings,
) : TimerSettingsRepository {

    override suspend fun getTimerSettings(): TimerSettings {
        val duration = normalizeDuration(settings.getInt(TOTAL_DURATION_KEY, 60))
        val breakDuration = normalizeBreakDuration(settings.getInt(BREAK_DURATION_KEY, 0))
        val intervalsRaw = settings.getString(INTERVALS_KEY, "")
        val parsed = intervalsRaw
            .split(",")
            .mapNotNull { it.toIntOrNull() }
            .toSet()
        return TimerSettings(
            totalDurationSeconds = duration,
            enabledIntervals = normalizeIntervals(duration, parsed),
            breakDurationSeconds = breakDuration,
        )
    }

    override suspend fun saveTimerSettings(settings: TimerSettings) {
        val safeDuration = normalizeDuration(settings.totalDurationSeconds)
        val safeIntervals = normalizeIntervals(safeDuration, settings.enabledIntervals)
        val safeBreakDuration = normalizeBreakDuration(settings.breakDurationSeconds)
        this.settings.putInt(TOTAL_DURATION_KEY, safeDuration)
        this.settings.putString(INTERVALS_KEY, safeIntervals.sorted().joinToString(","))
        this.settings.putInt(BREAK_DURATION_KEY, safeBreakDuration)
    }

    override suspend fun isOnboardingCompleted(): Boolean {
        return settings.getBoolean(ONBOARDING_KEY, false)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        settings.putBoolean(ONBOARDING_KEY, completed)
    }

    private companion object {
        private const val TOTAL_DURATION_KEY = "timer.totalDurationSeconds"
        private const val INTERVALS_KEY = "timer.enabledIntervals"
        private const val BREAK_DURATION_KEY = "timer.breakDurationSeconds"
        private const val ONBOARDING_KEY = "onboarding.completed"
    }
}
