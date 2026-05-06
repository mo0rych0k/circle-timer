package com.circle.timer.features.timer.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimerWidgetSnapshotTest {

    @Test
    fun `idle snapshot returns duration as remaining`() {
        val snapshot = idleTimerWidgetSnapshot(
            totalDurationSeconds = 30,
            breakDurationSeconds = 10,
        )

        val visuals = snapshot.resolveVisuals(nowEpochMillis = 1_000L)
        assertFalse(visuals.isRunning)
        assertEquals(TimerPhase.Active, visuals.phase)
        assertEquals(30, visuals.remainingSeconds)
        assertEquals(0f, visuals.progress)
    }

    @Test
    fun `running active phase computes remaining and progress`() {
        val snapshot = TimerWidgetSnapshot(
            isRunning = true,
            phase = TimerPhase.Active,
            phaseStartedEpochMillis = 10_000L,
            totalDurationSeconds = 60,
            breakDurationSeconds = 10,
        )

        val visuals = snapshot.resolveVisuals(nowEpochMillis = 25_000L)
        assertTrue(visuals.isRunning)
        assertEquals(TimerPhase.Active, visuals.phase)
        assertEquals(45, visuals.remainingSeconds)
        assertEquals(0.25f, visuals.progress)
    }

    @Test
    fun `running snapshot transitions to break when active completes`() {
        val snapshot = TimerWidgetSnapshot(
            isRunning = true,
            phase = TimerPhase.Active,
            phaseStartedEpochMillis = 1_000L,
            totalDurationSeconds = 10,
            breakDurationSeconds = 5,
        )

        val visuals = snapshot.resolveVisuals(nowEpochMillis = 12_000L)
        assertEquals(TimerPhase.Break, visuals.phase)
        assertEquals(4, visuals.remainingSeconds)
        assertEquals(0.2f, visuals.progress)
    }
}
