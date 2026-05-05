package com.circle.timer.features.timer.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class TimerSettingsTest {

    @Test
    fun allowedIntervals_matches_spec() {
        assertEquals(setOf(1, 5), allowedIntervalsForDuration(10))
        assertEquals(setOf(1, 5, 10), allowedIntervalsForDuration(15))
        assertEquals(setOf(1, 5, 10, 15), allowedIntervalsForDuration(30))
        assertEquals(setOf(1, 5, 10, 15, 30), allowedIntervalsForDuration(60))
    }

    @Test
    fun normalizeIntervals_allows_empty() {
        assertEquals(emptySet(), normalizeIntervals(60, emptySet()))
    }

    @Test
    fun normalizeIntervals_filters_to_allowed_and_drops_legacy_three() {
        assertEquals(setOf(5, 10), normalizeIntervals(60, setOf(3, 5, 10, 99)))
    }

    @Test
    fun normalizeIntervals_keeps_valid_selection() {
        val s = setOf(1, 15, 30)
        assertEquals(s, normalizeIntervals(60, s))
    }

    @Test
    fun normalizeBreakDuration_keeps_supported_values() {
        assertEquals(0, normalizeBreakDuration(0))
        assertEquals(5, normalizeBreakDuration(5))
        assertEquals(10, normalizeBreakDuration(10))
        assertEquals(15, normalizeBreakDuration(15))
        assertEquals(30, normalizeBreakDuration(30))
        assertEquals(60, normalizeBreakDuration(60))
    }

    @Test
    fun normalizeBreakDuration_falls_back_to_none() {
        assertEquals(0, normalizeBreakDuration(-1))
        assertEquals(0, normalizeBreakDuration(7))
        assertEquals(0, normalizeBreakDuration(120))
    }
}
