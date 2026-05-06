package com.circle.timer.features.timer.domain

public enum class DialNotchStyle {
    TickSmall,
    TickMedium,
    TickLarge,
    Triangle,
    Square,
}

public data class DialNotchModel(
    val second: Int,
    val style: DialNotchStyle,
    val highlightInterval: Int?,
)

public fun buildDialNotches(
    totalDurationSeconds: Int,
    enabledIntervals: Set<Int>,
): List<DialNotchModel> {
    val safeDuration = totalDurationSeconds.coerceAtLeast(1)
    return (1 until safeDuration).map { second ->
        DialNotchModel(
            second = second,
            style = notchStyleForSecond(second),
            highlightInterval = enabledIntervals.filter { second % it == 0 }.maxOrNull(),
        )
    }
}

public fun formatTimerCountdown(
    elapsedMillis: Long,
    totalDurationSeconds: Int,
    breakDurationSeconds: Int,
    isRunning: Boolean,
    phase: TimerPhase,
): String {
    val phaseDuration = when (phase) {
        TimerPhase.Active -> totalDurationSeconds
        TimerPhase.Break -> breakDurationSeconds
    }.coerceAtLeast(0)
    if (!isRunning) return totalDurationSeconds.toString()
    val elapsedSeconds = (elapsedMillis / 1000L).toInt()
    return (phaseDuration - elapsedSeconds).coerceAtLeast(0).toString()
}

public fun notchStyleForSecond(second: Int): DialNotchStyle {
    if (second % 30 == 0) return DialNotchStyle.Square
    if (second % 15 == 0) return DialNotchStyle.Triangle
    if (second % 10 == 0) return DialNotchStyle.TickLarge
    if (second % 5 == 0) return DialNotchStyle.TickMedium
    return DialNotchStyle.TickSmall
}
