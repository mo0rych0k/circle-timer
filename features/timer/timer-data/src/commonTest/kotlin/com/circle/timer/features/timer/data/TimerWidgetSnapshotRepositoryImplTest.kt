package com.circle.timer.features.timer.data

import com.circle.timer.features.timer.domain.TimerPhase
import com.circle.timer.features.timer.domain.TimerSettings
import com.circle.timer.features.timer.domain.TimerWidgetSnapshot
import com.russhwolf.settings.Settings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class TimerWidgetSnapshotRepositoryImplTest {
    @Test
    fun `returns idle snapshot when running flag is false`() = runBlocking {
        val settings = Settings()
        settings.clear()
        val repository = TimerWidgetSnapshotRepositoryImpl(settings)

        val snapshot = repository.getSnapshot(
            settings = TimerSettings(totalDurationSeconds = 30, breakDurationSeconds = 15),
        )
        assertFalse(snapshot.isRunning)
        assertEquals(30, snapshot.totalDurationSeconds)
        assertEquals(15, snapshot.breakDurationSeconds)
    }

    @Test
    fun `saves and loads running snapshot`() = runBlocking {
        val settings = Settings()
        settings.clear()
        val repository = TimerWidgetSnapshotRepositoryImpl(settings)
        val source = TimerWidgetSnapshot(
            isRunning = true,
            phase = TimerPhase.Break,
            phaseStartedEpochMillis = 4567L,
            totalDurationSeconds = 60,
            breakDurationSeconds = 10,
        )

        repository.saveSnapshot(source)

        val loaded = repository.getSnapshot(TimerSettings())
        assertTrue(loaded.isRunning)
        assertEquals(TimerPhase.Break, loaded.phase)
        assertEquals(4567L, loaded.phaseStartedEpochMillis)
        assertEquals(60, loaded.totalDurationSeconds)
        assertEquals(10, loaded.breakDurationSeconds)
    }
}
