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
            countdownLast3TimerEnabled = settings.getBoolean(COUNTDOWN_LAST3_TIMER_KEY, true),
            countdownLast3BreakEnabled = settings.getBoolean(COUNTDOWN_LAST3_BREAK_KEY, true),
            notificationPermissionPromptShown = settings.getBoolean(NOTIFICATION_PROMPT_SHOWN_KEY, false),
        )
    }

    override suspend fun saveTimerSettings(settings: TimerSettings) {
        val safeDuration = normalizeDuration(settings.totalDurationSeconds)
        val safeIntervals = normalizeIntervals(safeDuration, settings.enabledIntervals)
        val safeBreakDuration = normalizeBreakDuration(settings.breakDurationSeconds)
        this.settings.putInt(TOTAL_DURATION_KEY, safeDuration)
        this.settings.putString(INTERVALS_KEY, safeIntervals.sorted().joinToString(","))
        this.settings.putInt(BREAK_DURATION_KEY, safeBreakDuration)
        this.settings.putBoolean(COUNTDOWN_LAST3_TIMER_KEY, settings.countdownLast3TimerEnabled)
        this.settings.putBoolean(COUNTDOWN_LAST3_BREAK_KEY, settings.countdownLast3BreakEnabled)
        this.settings.putBoolean(NOTIFICATION_PROMPT_SHOWN_KEY, settings.notificationPermissionPromptShown)
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
        private const val COUNTDOWN_LAST3_TIMER_KEY = "timer.countdown.last3.timer"
        private const val COUNTDOWN_LAST3_BREAK_KEY = "timer.countdown.last3.break"
        private const val NOTIFICATION_PROMPT_SHOWN_KEY = "timer.notification.promptShown"
        private const val ONBOARDING_KEY = "onboarding.completed"
    }
}
