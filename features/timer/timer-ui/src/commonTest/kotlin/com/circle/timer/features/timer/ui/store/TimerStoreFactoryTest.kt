package com.circle.timer.features.timer.ui.store

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.circle.timer.features.timer.domain.TimerAudioPlayer
import com.circle.timer.features.timer.domain.TimerSettings
import com.circle.timer.features.timer.domain.TimerSettingsRepository
import com.circle.timer.features.timer.domain.TimerWidgetSnapshot
import com.circle.timer.features.timer.domain.TimerWidgetSnapshotRepository
import com.circle.timer.features.timer.domain.idleTimerWidgetSnapshot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest

class TimerStoreFactoryTest {

    @Test
    fun editor_changes_stay_draft_until_save() = runTest {
        val repository = FakeRepository(
            initial = TimerSettings(
                totalDurationSeconds = 60,
                enabledIntervals = setOf(5),
                breakDurationSeconds = 0,
            ),
        )
        val store = createStore(repository = repository)
        awaitLoaded(store, expectedDuration = 60)

        store.accept(TimerStore.Intent.OpenEditor)
        store.accept(TimerStore.Intent.UpdateDuration(15))
        store.accept(TimerStore.Intent.UpdateBreakDuration(10))

        assertEquals(60, store.state.appliedSettings.totalDurationSeconds)
        assertEquals(15, store.state.draftSettings.totalDurationSeconds)
        assertEquals(0, store.state.appliedSettings.breakDurationSeconds)
        assertEquals(10, store.state.draftSettings.breakDurationSeconds)

        store.dispose()
    }

    @Test
    fun close_editor_discards_unsaved_draft() = runTest {
        val repository = FakeRepository(initial = TimerSettings(totalDurationSeconds = 30, breakDurationSeconds = 0))
        val store = createStore(repository = repository)
        awaitLoaded(store, expectedDuration = 30)

        store.accept(TimerStore.Intent.OpenEditor)
        store.accept(TimerStore.Intent.UpdateDuration(60))
        store.accept(TimerStore.Intent.UpdateBreakDuration(15))
        store.accept(TimerStore.Intent.CloseEditor)
        delay(100)

        assertEquals(30, store.state.appliedSettings.totalDurationSeconds)
        assertEquals(30, store.state.draftSettings.totalDurationSeconds)
        assertEquals(0, store.state.draftSettings.breakDurationSeconds)

        store.dispose()
    }

    @Test
    fun save_while_running_stops_and_resets() = runTest {
        val repository = FakeRepository(initial = TimerSettings(totalDurationSeconds = 10, breakDurationSeconds = 0))
        val store = createStore(repository = repository)
        awaitLoaded(store, expectedDuration = 10)

        store.accept(TimerStore.Intent.ToggleRun)
        delay(100)

        store.accept(TimerStore.Intent.OpenEditor)
        store.accept(TimerStore.Intent.UpdateBreakDuration(5))
        store.accept(TimerStore.Intent.SaveSettings)
        delay(100)
        repeat(20) {
            if (repository.saved.isNotEmpty()) return@repeat
            delay(25)
        }

        assertFalse(store.state.isRunning)
        assertEquals(0L, store.state.elapsedMillis)
        assertEquals(TimerStore.TimerPhase.Active, store.state.phase)
        assertEquals(5, store.state.appliedSettings.breakDurationSeconds)
        assertEquals(5, repository.saved.lastOrNull()?.breakDurationSeconds)

        store.dispose()
    }

    private fun createStore(repository: FakeRepository): TimerStore =
        TimerStoreFactory(
            storeFactory = DefaultStoreFactory(),
            repository = repository,
            snapshotRepository = FakeSnapshotRepository(repository.initial),
            audioPlayer = NoOpAudioPlayer(),
        ).create().also { it.init() }

    private suspend fun awaitLoaded(store: TimerStore, expectedDuration: Int) {
        repeat(80) {
            if (store.state.appliedSettings.totalDurationSeconds == expectedDuration) {
                return
            }
            delay(25)
        }
        assertTrue(
            store.state.appliedSettings.totalDurationSeconds == expectedDuration,
            "Store did not load expected duration in time",
        )
    }

    private class NoOpAudioPlayer : TimerAudioPlayer {
        override fun playInterval(intervalSeconds: Int) = Unit
        override fun playCountdown(isBreak: Boolean, secondsRemaining: Int) = Unit
        override fun playCycleComplete() = Unit
        override fun stop() = Unit
    }

    private class FakeRepository(
        val initial: TimerSettings,
    ) : TimerSettingsRepository {
        val saved: MutableList<TimerSettings> = mutableListOf()

        override suspend fun getTimerSettings(): TimerSettings = initial

        override suspend fun saveTimerSettings(settings: TimerSettings) {
            saved += settings
        }

        override suspend fun isOnboardingCompleted(): Boolean = true

        override suspend fun setOnboardingCompleted(completed: Boolean) = Unit
    }

    private class FakeSnapshotRepository(
        settings: TimerSettings,
    ) : TimerWidgetSnapshotRepository {
        private var snapshot: TimerWidgetSnapshot = idleTimerWidgetSnapshot(
            totalDurationSeconds = settings.totalDurationSeconds,
            breakDurationSeconds = settings.breakDurationSeconds,
        )

        override suspend fun getSnapshot(settings: TimerSettings): TimerWidgetSnapshot = snapshot

        override suspend fun saveSnapshot(snapshot: TimerWidgetSnapshot) {
            this.snapshot = snapshot
        }
    }
}
