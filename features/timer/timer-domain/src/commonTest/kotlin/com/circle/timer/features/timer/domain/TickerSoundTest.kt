package com.circle.timer.features.timer.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TickerSoundTest {

    @Test
    fun cycle_complete_wins_over_interval_on_same_tick() {
        val durationMs = 10_000L
        val previous = 9_984L
        val elapsed = 10_000L
        val r = tickerSoundResult(
            previousMillis = previous,
            elapsedMillis = elapsed,
            durationMillis = durationMs,
            enabledIntervals = setOf(1, 5, 10),
        )
        assertTrue(r.cycleComplete)
        assertNull(r.intervalSeconds)
    }

    @Test
    fun empty_enabled_intervals_never_emits_interval_sound() {
        val r = tickerSoundResult(
            previousMillis = 5_000L,
            elapsedMillis = 5_016L,
            durationMillis = 60_000L,
            enabledIntervals = emptySet(),
        )
        assertFalse(r.cycleComplete)
        assertNull(r.intervalSeconds)
    }

    @Test
    fun interval_crossing_emits_largest_major_interval() {
        val r = tickerSoundResult(
            previousMillis = 4_980L,
            elapsedMillis = 5_000L,
            durationMillis = 60_000L,
            enabledIntervals = setOf(1, 5),
        )
        assertFalse(r.cycleComplete)
        assertEquals(5, r.intervalSeconds)
    }

    @Test
    fun one_second_only_when_no_major_crossed() {
        val r = tickerSoundResult(
            previousMillis = 980L,
            elapsedMillis = 1_000L,
            durationMillis = 60_000L,
            enabledIntervals = setOf(1, 5),
        )
        assertFalse(r.cycleComplete)
        assertEquals(1, r.intervalSeconds)
    }
}
