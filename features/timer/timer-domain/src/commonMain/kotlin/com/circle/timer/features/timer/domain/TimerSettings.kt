package com.circle.timer.features.timer.domain

public data class TimerSettings(
    val totalDurationSeconds: Int = 60,
    val enabledIntervals: Set<Int> = emptySet(),
    val breakDurationSeconds: Int = 0,
    val countdownLast3TimerEnabled: Boolean = true,
    val countdownLast3BreakEnabled: Boolean = true,
    val notificationPermissionPromptShown: Boolean = false,
)

public val supportedDurations: Set<Int> = setOf(10, 15, 30, 60)

public fun allowedIntervalsForDuration(totalDurationSeconds: Int): Set<Int> = when (totalDurationSeconds) {
    10 -> setOf(1, 5)
    15 -> setOf(1, 5, 10)
    30 -> setOf(1, 5, 10, 15)
    60 -> setOf(1, 5, 10, 15, 30)
    else -> setOf(5)
}

public fun normalizeDuration(totalDurationSeconds: Int): Int =
    if (supportedDurations.contains(totalDurationSeconds)) totalDurationSeconds else 60

public fun normalizeIntervals(
    totalDurationSeconds: Int,
    enabledIntervals: Set<Int>,
): Set<Int> {
    val allowed = allowedIntervalsForDuration(totalDurationSeconds)
    return enabledIntervals.intersect(allowed)
}

public val supportedBreakDurations: Set<Int> = setOf(0, 5, 10, 15, 30, 60)

public fun normalizeBreakDuration(breakDurationSeconds: Int): Int =
    if (supportedBreakDurations.contains(breakDurationSeconds)) breakDurationSeconds else 0
