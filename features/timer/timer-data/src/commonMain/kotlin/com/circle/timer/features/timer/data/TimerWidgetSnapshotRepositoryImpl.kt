package com.circle.timer.features.timer.data

import com.circle.timer.features.timer.domain.TimerPhase
import com.circle.timer.features.timer.domain.TimerSettings
import com.circle.timer.features.timer.domain.TimerWidgetSnapshot
import com.circle.timer.features.timer.domain.TimerWidgetSnapshotRepository
import com.circle.timer.features.timer.domain.idleTimerWidgetSnapshot
import com.circle.timer.features.timer.domain.normalizeBreakDuration
import com.circle.timer.features.timer.domain.normalizeDuration
import com.russhwolf.settings.Settings

internal class TimerWidgetSnapshotRepositoryImpl(
    private val settings: Settings,
) : TimerWidgetSnapshotRepository {
    override suspend fun getSnapshot(settings: TimerSettings): TimerWidgetSnapshot {
        val isRunning = this.settings.getBoolean(IS_RUNNING_KEY, false)
        if (!isRunning) {
            return idleTimerWidgetSnapshot(
                totalDurationSeconds = settings.totalDurationSeconds,
                breakDurationSeconds = settings.breakDurationSeconds,
            )
        }
        val phaseValue = this.settings.getString(PHASE_KEY, TimerPhase.Active.name)
        val phase = runCatching { TimerPhase.valueOf(phaseValue) }.getOrDefault(TimerPhase.Active)
        return TimerWidgetSnapshot(
            isRunning = true,
            phase = phase,
            phaseStartedEpochMillis = this.settings.getLong(PHASE_STARTED_AT_EPOCH_MILLIS, 0L),
            totalDurationSeconds = normalizeDuration(
                this.settings.getInt(
                    TOTAL_DURATION_KEY,
                    settings.totalDurationSeconds,
                ),
            ),
            breakDurationSeconds = normalizeBreakDuration(
                this.settings.getInt(
                    BREAK_DURATION_KEY,
                    settings.breakDurationSeconds,
                ),
            ),
        )
    }

    override suspend fun saveSnapshot(snapshot: TimerWidgetSnapshot) {
        settings.putBoolean(IS_RUNNING_KEY, snapshot.isRunning)
        settings.putString(PHASE_KEY, snapshot.phase.name)
        settings.putLong(PHASE_STARTED_AT_EPOCH_MILLIS, snapshot.phaseStartedEpochMillis)
        settings.putInt(TOTAL_DURATION_KEY, normalizeDuration(snapshot.totalDurationSeconds))
        settings.putInt(BREAK_DURATION_KEY, normalizeBreakDuration(snapshot.breakDurationSeconds))
    }

    private companion object {
        private const val ROOT = "timer.widget.snapshot"
        private const val IS_RUNNING_KEY = "$ROOT.isRunning"
        private const val PHASE_KEY = "$ROOT.phase"
        private const val PHASE_STARTED_AT_EPOCH_MILLIS = "$ROOT.phaseStartedAtEpochMillis"
        private const val TOTAL_DURATION_KEY = "$ROOT.totalDurationSeconds"
        private const val BREAK_DURATION_KEY = "$ROOT.breakDurationSeconds"
    }
}
