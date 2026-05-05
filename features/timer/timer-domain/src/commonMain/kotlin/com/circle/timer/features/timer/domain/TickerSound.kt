package com.circle.timer.features.timer.domain

/**
 * Pure decision for which sound (if any) should play after advancing the clock by one tick.
 * Cycle completion takes precedence over interval ticks on the same step.
 */
public data class TickerSoundResult(
    public val cycleComplete: Boolean,
    public val intervalSeconds: Int?,
)

public fun tickerSoundResult(
    previousMillis: Long,
    elapsedMillis: Long,
    durationMillis: Long,
    enabledIntervals: Set<Int>,
): TickerSoundResult {
    if (durationMillis <= 0L) return TickerSoundResult(cycleComplete = false, intervalSeconds = null)
    if (elapsedMillis >= durationMillis) {
        return TickerSoundResult(cycleComplete = true, intervalSeconds = null)
    }
    if (enabledIntervals.isEmpty()) {
        return TickerSoundResult(cycleComplete = false, intervalSeconds = null)
    }
    val crossed = enabledIntervals.filter { interval ->
        val marker = interval * 1000L
        marker > 0L && (previousMillis / marker) < (elapsedMillis / marker)
    }
    if (crossed.isEmpty()) return TickerSoundResult(cycleComplete = false, intervalSeconds = null)
    val majorInterval = crossed.filter { it >= 3 }.maxOrNull()
    val interval = when {
        majorInterval != null -> majorInterval
        crossed.contains(1) -> 1
        else -> null
    }
    return TickerSoundResult(cycleComplete = false, intervalSeconds = interval)
}
