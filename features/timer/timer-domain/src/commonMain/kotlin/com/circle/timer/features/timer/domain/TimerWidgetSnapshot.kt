package com.circle.timer.features.timer.domain

public enum class TimerPhase {
    Active,
    Break,
}

public data class TimerWidgetSnapshot(
    val isRunning: Boolean,
    val phase: TimerPhase,
    val phaseStartedEpochMillis: Long,
    val totalDurationSeconds: Int,
    val breakDurationSeconds: Int,
) {
    public fun resolveVisuals(nowEpochMillis: Long): TimerWidgetVisuals {
        val safeDuration = normalizeDuration(totalDurationSeconds)
        val safeBreak = normalizeBreakDuration(breakDurationSeconds)
        if (!isRunning) {
            return TimerWidgetVisuals(
                isRunning = false,
                phase = TimerPhase.Active,
                remainingSeconds = safeDuration,
                progress = 0f,
            )
        }
        val elapsedMillis = (nowEpochMillis - phaseStartedEpochMillis).coerceAtLeast(0L)
        val resolved = resolvePhase(elapsedMillis, phase, safeDuration, safeBreak)
        return TimerWidgetVisuals(
            isRunning = true,
            phase = resolved.phase,
            remainingSeconds = resolved.remainingSeconds,
            progress = resolved.progress,
        )
    }

    private fun resolvePhase(
        elapsedMillis: Long,
        initialPhase: TimerPhase,
        activeDurationSeconds: Int,
        breakDurationSeconds: Int,
    ): TimerWidgetVisuals {
        val activeMillis = activeDurationSeconds * 1000L
        val breakMillis = breakDurationSeconds * 1000L
        if (initialPhase == TimerPhase.Active) {
            if (elapsedMillis < activeMillis) {
                return computeVisuals(TimerPhase.Active, activeDurationSeconds, elapsedMillis)
            }
            if (breakMillis <= 0L) {
                val inCycle = elapsedMillis % activeMillis
                return computeVisuals(TimerPhase.Active, activeDurationSeconds, inCycle)
            }
            val afterActive = elapsedMillis - activeMillis
            val cycleMillis = activeMillis + breakMillis
            val inCycle = afterActive % cycleMillis
            return if (inCycle < breakMillis) {
                computeVisuals(TimerPhase.Break, breakDurationSeconds, inCycle)
            } else {
                computeVisuals(TimerPhase.Active, activeDurationSeconds, inCycle - breakMillis)
            }
        }

        if (breakMillis <= 0L) {
            val inCycle = elapsedMillis % activeMillis
            return computeVisuals(TimerPhase.Active, activeDurationSeconds, inCycle)
        }
        if (elapsedMillis < breakMillis) {
            return computeVisuals(TimerPhase.Break, breakDurationSeconds, elapsedMillis)
        }
        val afterBreak = elapsedMillis - breakMillis
        val cycleMillis = activeMillis + breakMillis
        val inCycle = afterBreak % cycleMillis
        return if (inCycle < activeMillis) {
            computeVisuals(TimerPhase.Active, activeDurationSeconds, inCycle)
        } else {
            computeVisuals(TimerPhase.Break, breakDurationSeconds, inCycle - activeMillis)
        }
    }
}

public data class TimerWidgetVisuals(
    val isRunning: Boolean,
    val phase: TimerPhase,
    val remainingSeconds: Int,
    val progress: Float,
)

public fun idleTimerWidgetSnapshot(
    totalDurationSeconds: Int,
    breakDurationSeconds: Int,
): TimerWidgetSnapshot {
    val safeDuration = normalizeDuration(totalDurationSeconds)
    return TimerWidgetSnapshot(
        isRunning = false,
        phase = TimerPhase.Active,
        phaseStartedEpochMillis = 0L,
        totalDurationSeconds = safeDuration,
        breakDurationSeconds = normalizeBreakDuration(breakDurationSeconds),
    )
}

private fun computeVisuals(
    phase: TimerPhase,
    phaseDurationSeconds: Int,
    phaseElapsedMillis: Long,
): TimerWidgetVisuals {
    val safeDuration = phaseDurationSeconds.coerceAtLeast(0)
    val elapsedSeconds = (phaseElapsedMillis / 1000L).toInt().coerceAtLeast(0)
    val remaining = (safeDuration - elapsedSeconds).coerceAtLeast(0)
    val progress =
        if (safeDuration == 0) 0f else (phaseElapsedMillis.toFloat() / (safeDuration * 1000L).toFloat()).coerceIn(
            0f,
            1f,
        )
    return TimerWidgetVisuals(
        isRunning = true,
        phase = phase,
        remainingSeconds = remaining,
        progress = progress,
    )
}
