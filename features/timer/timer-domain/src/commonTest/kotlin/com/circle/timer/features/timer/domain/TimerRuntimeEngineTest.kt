package com.circle.timer.features.timer.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimerRuntimeEngineTest {

    @Test
    fun first_second_interval_is_emitted_exactly_once() {
        val engine = TimerRuntimeEngine(
            config = RuntimeConfig(
                totalDurationSeconds = 10,
                breakDurationSeconds = 0,
                enabledIntervals = setOf(1),
                countdownLast3TimerEnabled = false,
                countdownLast3BreakEnabled = false,
            ),
        )
        engine.start(nowEpochMillis = 0L)
        val step = engine.step(nowEpochMillis = 1_005L)

        val cue = step.cue
        assertTrue(cue is RuntimeCue.IntervalTick)
        assertEquals(1, cue.intervalSeconds)
    }

    @Test
    fun countdown_overrides_regular_interval_in_last_three_seconds() {
        val engine = TimerRuntimeEngine(
            config = RuntimeConfig(
                totalDurationSeconds = 10,
                breakDurationSeconds = 0,
                enabledIntervals = setOf(1, 5),
                countdownLast3TimerEnabled = true,
                countdownLast3BreakEnabled = false,
            ),
        )
        engine.start(nowEpochMillis = 0L)
        val step = engine.step(nowEpochMillis = 7_050L)

        val cue = step.cue
        assertTrue(cue is RuntimeCue.CountdownTick)
        assertEquals(RuntimePhase.Active, cue.phase)
        assertEquals(3, cue.secondsRemaining)
    }

    @Test
    fun phase_complete_has_highest_priority() {
        val engine = TimerRuntimeEngine(
            config = RuntimeConfig(
                totalDurationSeconds = 5,
                breakDurationSeconds = 5,
                enabledIntervals = setOf(1),
                countdownLast3TimerEnabled = true,
                countdownLast3BreakEnabled = true,
            ),
        )
        engine.start(nowEpochMillis = 0L)
        val step = engine.step(nowEpochMillis = 5_000L)
        assertEquals(RuntimeCue.PhaseComplete, step.cue)
        assertEquals(RuntimePhase.Break, step.state.phase)
    }
}
